package com.example.weatherproject

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.weatherproject.db.WeatherDbHelper
import com.example.weatherproject.db.WeatherRepository
import com.example.weatherproject.models.City
import com.example.weatherproject.models.DailyWeather
import com.example.weatherproject.models.HourlyWeather
import com.example.weatherproject.network.WeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.getDefault())
val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val dbRepository = WeatherRepository(application)

    var hourlyList by mutableStateOf<List<HourlyWeather>>(emptyList())
        private set
    var dailyList by mutableStateOf<List<DailyWeather>>(emptyList())
        private set
    var cityResult by mutableStateOf<City?>(null)
        private set
    var errorMessage by mutableStateOf("")

    suspend fun fetchHourlyWeather(inputCity: String): List<HourlyWeather> {
        val location = WeatherNetwork.getLocationInfo(inputCity) ?: return emptyList()
        val hourData = WeatherNetwork.getHourWeatherById(location.id)

        if (hourData?.optString("code") == "200") {
            val hourlyArray = hourData.getJSONArray("hourly")
            return List(hourlyArray.length()) { i ->
                val hourObj = hourlyArray.getJSONObject(i)
                HourlyWeather(
                    time = outputFormat.format(inputFormat.parse(hourObj.getString("fxTime"))!!),
                    temperature = hourObj.getString("temp"),
                    status = hourObj.getString("text"),
                )
            }
        }

        return emptyList()
    }

    suspend fun fetchDailyWeather(inputCity: String): List<DailyWeather> {
        val location = WeatherNetwork.getLocationInfo(inputCity) ?: return emptyList()
        val dailyData = WeatherNetwork.getWeekWeatherById(location.id)

        if (dailyData?.optString("code") == "200") {
            val dailyArray = dailyData.getJSONArray("daily")
            return List(dailyArray.length()) { i ->
                val dailyObj = dailyArray.getJSONObject(i)
                DailyWeather(
                    day = dailyObj.getString("fxDate"),
                    status = "${dailyObj.getString("textDay")}~${dailyObj.getString("textNight")}",
                    temperatureRange = "${dailyObj.getString("tempMin")}~${dailyObj.getString("tempMax")}",
                    iconDayResId = dailyObj.getString("iconDay"),
                    iconNightResId = dailyObj.getString("iconNight")
                )
            }
        }

        return emptyList()
    }

    suspend fun fetchCitySummary(inputCity: String): City? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val cached = withContext(Dispatchers.IO) {
            dbRepository.getCachedWeather(inputCity, today)
        }

        if (cached != null) {
            Log.d("Weather", "缓存命中: $cached")
            return City(
                position = cached.city,
                weather = cached.condition,
                wendu = cached.temperature,
                cleathe = cached.airQuality
            )
        }

        val location = WeatherNetwork.getLocationInfo(inputCity) ?: return null
        val locationId = location.id

        val nowData = WeatherNetwork.getNowWeatherById(locationId)
        val airData = WeatherNetwork.getAirConditionById(inputCity)

        val now = nowData?.getJSONObject("now")
        val temp = now?.getString("temp")?.plus("°")
        val cond = now?.getString("text")

        val indexesArray = airData?.optJSONArray("indexes")
        val category = indexesArray?.optJSONObject(0)?.optString("category") ?: "暂无数据"
        val airFull = "空气$category"

        // 添加调试日志
        Log.d("FetchCitySummary", "temp: $temp")
        Log.d("FetchCitySummary", "cond: $cond")
        Log.d("FetchCitySummary", "category: $category")
        Log.d("FetchCitySummary", "airFull: $airFull")

        val city = cond?.let {
            City(inputCity, it, temp ?: "", airFull)
        }

        withContext(Dispatchers.IO) {
            dbRepository.insertCity(inputCity)
        }

        return city
    }
//    fun fetchCityWeather(inputCity: String) {
//        viewModelScope.launch {
//            return@launch try {
//                errorMessage = ""
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val today = dateFormat.format(Date())
//                val cached = withContext(Dispatchers.IO) {
//                    dbRepository.getCachedWeather(inputCity, today)
//                }
//
//                if (cached != null) {
//                    Log.d("Weather", "缓存命中: $cached")
//                    // TODO: 可根据实际缓存结构还原 daily/hourly
//                    return@launch
//                }
//
//                val location = WeatherNetwork.getLocationInfo(inputCity)
//                if (location == null) {
//                    errorMessage = "找不到城市位置"
//                    return@launch
//                }
//
//                val locationId = location.id
//                val nowData = WeatherNetwork.getNowWeatherById(locationId)
//                val hourData = WeatherNetwork.getHourWeatherById(locationId)
//                val dailyData = WeatherNetwork.getWeekWeatherById(locationId)
//                val airData = WeatherNetwork.getAirConditionById(inputCity)
//
//                // 获取小时天气
//                if (hourData?.optString("code") == "200") {
//                    val hourlyArray = hourData.getJSONArray("hourly")
//                    val tempList = mutableListOf<HourlyWeather>()
//                    for (i in 0 until hourlyArray.length()) {
//                        val hourObj = hourlyArray.getJSONObject(i)
//                        tempList.add(
//                            HourlyWeather(
//                                time = outputFormat.format(inputFormat.parse(hourObj.getString("fxTime"))!!),
//                                temperature = hourObj.getString("temp"),
//                                status = hourObj.getString("text"),
//                            )
//                        )
//                    }
//                    Log.d("hourData","小时获取成功")
//                    hourlyList = tempList
//                } else {
//                    errorMessage = "小时天气信息获取失败"
//                    return@launch
//                }
//
//                // 获取每日天气
//                if (dailyData?.optString("code") == "200") {
//                    val dailyArray = dailyData.getJSONArray("daily")
//                    val tempList = mutableListOf<DailyWeather>()
//                    for (i in 0 until dailyArray.length()) {
//                        val dailyObj = dailyArray.getJSONObject(i)
//                        tempList.add(
//                            DailyWeather(
//                                day = dailyObj.getString("fxDate"),
//                                status = dailyObj.getString("textDay") + "~" + dailyObj.getString("textNight"),
//                                temperatureRange = dailyObj.getString("tempMin") + "~" + dailyObj.getString("tempMax"),
//                                iconDayResId = dailyObj.getString("iconDay"),
//                                iconNightResId = dailyObj.getString("iconNight")
//                            )
//                        )
//                    }
//                    Log.d("dailyData","每天获取成功")
//                    dailyList = tempList
//                } else {
//                    errorMessage = "一周天气信息获取失败"
//                    return@launch
//                }
//
//                // 构建城市信息
//                val now = nowData?.getJSONObject("now")
//                val temp = now?.getString("temp") + "°"
//                val cond = now?.getString("text")
//                val air = airData?.getJSONObject("indexes")?.getJSONObject("category")?: "暂无数据"
//                val airFull = "空气$air"
//
//                cityResult = cond?.let { City(inputCity, it, temp, airFull) }
//
//                // 缓存数据（需要你自己封装保存结构）
//                withContext(Dispatchers.IO) {
//                    dbRepository.insertCity(inputCity)
//                }
//            } catch (e: Exception) {
//                errorMessage = "网络或数据异常"
//                e.printStackTrace()
//            }
//        }
//    }
}