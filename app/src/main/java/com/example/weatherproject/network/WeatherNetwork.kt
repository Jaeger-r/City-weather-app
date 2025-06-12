package com.example.weatherproject.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import android.util.Log
import java.net.URLEncoder
import kotlin.math.round

object WeatherNetwork {
    private val client = OkHttpClient()
    private const val apiKey = "5b706639f9ac41518406d1f3bb8338bd"
    private const val baseUrl = "https://qj5vxkyetq.re.qweatherapi.com"
    private const val weatherPredictPerHour = "24h"
    private const val weatherPredictPerDay = "7d"
    data class CityLocation(
        val id: String,
        val longitude: String,
        val latitude: String
    )
    // 城市查询：根据城市名获取ID，经纬度
//    val cityLocation = getLocationInfo("北京")
//    if (cityLocation != null) {
//        val locationId = cityLocation.id
//        val lon = cityLocation.longitude
//        val lat = cityLocation.latitude
//        // 你可以使用这些信息了
//    } else {
//        // 查询失败
//    }

    suspend fun getLocationInfo(cityName: String): CityLocation? = withContext(Dispatchers.IO) {
        try {
            val encodedCity = URLEncoder.encode(cityName, "UTF-8")
            val url = "$baseUrl/geo/v2/city/lookup?location=$encodedCity&key=$apiKey"
            val request = Request.Builder().url(url).build()

            Log.d("GetLocationInfo", "inputCity=$cityName")
            Log.d("GetLocationInfo", "requesting URL: $url")

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string()
                Log.d("GetLocationInfo", "Response JSON: $bodyStr")

                val json = JSONObject(bodyStr)
                val code = json.optString("code")
                Log.d("GetLocationInfo", "API 返回 code: $code")

                val locationArray = json.optJSONArray("location")
                if (locationArray != null && locationArray.length() > 0) {
                    val first = locationArray.getJSONObject(0)
                    val cityLocation = CityLocation(
                        id = first.optString("id", ""),
                        longitude = first.optString("lon", ""),
                        latitude = first.optString("lat", "")
                    )
                    Log.d("GetLocationInfo", "Parsed CityLocation: $cityLocation")
                    cityLocation
                } else {
                    Log.d("GetLocationInfo", "No location data found")
                    null
                }
            } else {
                Log.d("GetLocationInfo", "Response failed with code: ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e("GetLocationInfo", "Exception during request", e)
            null
        }
    }
    // 空气质量查询：根据 locationId
    suspend fun getAirConditionById(cityName: String): JSONObject? = withContext(Dispatchers.IO) {

        val cityLocation = getLocationInfo(cityName)
        if (cityLocation == null) {
            return@withContext null // 查询失败
        }

        val lon = String.format("%.2f", cityLocation.longitude.toFloat())
        val lat = String.format("%.2f", cityLocation.latitude.toFloat())

        val url = "$baseUrl/airquality/v1/current/$lat/$lon?&key=$apiKey"
        Log.d("getAirConditionById", "requesting URL: $url")
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string()
                if (bodyStr != null) {
                    JSONObject(bodyStr)
                } else null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    // 24H天气查询：根据 locationId
    suspend fun getHourWeatherById(locationId: String): JSONObject? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/v7/weather/$weatherPredictPerHour?location=$locationId&key=$apiKey"
        Log.d("getHourWeatherById", "requesting URL: $url")
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string()
                JSONObject(bodyStr)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 一周天气查询：根据 locationId
    suspend fun getWeekWeatherById(locationId: String): JSONObject? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/v7/weather/$weatherPredictPerDay?location=$locationId&key=$apiKey"
        Log.d("getWeekWeatherById", "requesting URL: $url")
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string()
                JSONObject(bodyStr)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    //实时天气查询
    suspend fun getNowWeatherById(locationId: String): JSONObject? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/v7/weather/now?location=$locationId&key=$apiKey"
        Log.d("getNowWeatherById", "requesting URL: $url")
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string()
                JSONObject(bodyStr)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}