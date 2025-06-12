package com.example.weatherproject.db

import android.content.Context
import com.example.weatherproject.db.WeatherData
import com.example.weatherproject.db.WeatherRepository
import org.json.JSONObject

object WeatherJsonParser {

    /**
     * 解析天气 JSON 并保存到数据库
     * JSON 格式示例：
     * {
     *   "city": "Beijing",
     *   "date": "2025-06-09",
     *   "condition": "晴",
     *   "temperature": "28℃",
     *   "humidity": "40%",
     *   "windDirection": "东风",
     *   "windPower": "3级"
     * }
     */
    fun parseJsonAndSave(context: Context, json: String) {
        try {
            val jsonObj = JSONObject(json)

            val city = jsonObj.getString("city")
            val date = jsonObj.getString("date")
            val condition = jsonObj.getString("condition")
            val temperature = jsonObj.getString("temperature")
            val humidity = jsonObj.getString("humidity")
            val windDirection = jsonObj.getString("windDirection")
            val windPower = jsonObj.getString("windPower")

            val weatherData = WeatherData(
                city = city,
                date = date,
                condition = condition,
                temperature = temperature,
                airQuality = humidity,
                windDirection = windDirection,
                windPower = windPower
            )

            val repo = WeatherRepository(context)
            //保存数据
            repo.saveWeather(weatherData)

        } catch (e: Exception) {
            e.printStackTrace()
            // 这里可以做异常处理，比如日志记录，或返回错误状态
        }
    }
}
