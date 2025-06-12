package com.example.weatherproject.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class WeatherRepository(context: Context) {

    private val dbHelper = WeatherDbHelper(context)

    // 查询缓存
    fun getCachedWeather(city: String, date: String): WeatherData? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WeatherDbHelper.TABLE_WEATHER,
            null,
            "${WeatherDbHelper.COL_CITY}=? AND ${WeatherDbHelper.COL_DATE}=?",
            arrayOf(city, date),
            null, null, null
        )
        var weather: WeatherData? = null
        if (cursor.moveToFirst()) {
            weather = WeatherData(
                city = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COL_CITY)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COL_DATE)),
                condition = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COL_CONDITION)),
                temperature = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COL_TEMP)),
                airQuality = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COL_AIRQUALITY)),
                windDirection = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COL_WIND_DIRECTION)),
                windPower = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COL_WIND_POWER))
            )
        }
        cursor.close()
        return weather
    }

    // 保存天气数据（插入或替换）
    fun saveWeather(weather: WeatherData) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WeatherDbHelper.COL_CITY, weather.city)
            put(WeatherDbHelper.COL_DATE, weather.date)
            put(WeatherDbHelper.COL_CONDITION, weather.condition)
            put(WeatherDbHelper.COL_TEMP, weather.temperature)
            put(WeatherDbHelper.COL_AIRQUALITY, weather.airQuality)
            put(WeatherDbHelper.COL_WIND_DIRECTION, weather.windDirection)
            put(WeatherDbHelper.COL_WIND_POWER, weather.windPower)
        }
        db.replace(WeatherDbHelper.TABLE_WEATHER, null, values)
    }
    fun insertCity(city: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WeatherDbHelper.COL_CITY_NAME, city)
        }
        db.insertWithOnConflict(WeatherDbHelper.TABLE_CITY, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun getAllCities(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WeatherDbHelper.TABLE_CITY,
            arrayOf(WeatherDbHelper.COL_CITY_NAME),
            null, null, null, null, null
        )

        val result = mutableListOf<String>()
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0))
        }
        cursor.close()
        return result
    }
    fun deleteCity(cityName: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            WeatherDbHelper.TABLE_CITY,
            "${WeatherDbHelper.COL_CITY_NAME} = ?",
            arrayOf(cityName)
        )
    }
}
