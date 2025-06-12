package com.example.weatherproject.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WeatherDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "weather.db"
        private const val DATABASE_VERSION = 1

        // weather 表字段
        const val TABLE_WEATHER = "weather"
        const val COL_CITY = "city"
        const val COL_DATE = "date"
        const val COL_CONDITION = "condition"
        const val COL_TEMP = "temperature"
        const val COL_AIRQUALITY = "airQuality"
        const val COL_WIND_DIRECTION = "wind_direction"
        const val COL_WIND_POWER = "wind_power"

        // city_table 表字段
        const val TABLE_CITY = "city_table"
        const val COL_CITY_NAME = "city"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建 weather 表
        val createWeatherTableSql = """
            CREATE TABLE $TABLE_WEATHER (
                $COL_CITY TEXT,
                $COL_DATE TEXT,
                $COL_CONDITION TEXT,
                $COL_TEMP TEXT,
                $COL_AIRQUALITY TEXT,
                $COL_WIND_DIRECTION TEXT,
                $COL_WIND_POWER TEXT,
                PRIMARY KEY ($COL_CITY, $COL_DATE)
            )
        """.trimIndent()

        // 创建 city_table 表
        val createCityTableSql = """
            CREATE TABLE $TABLE_CITY (
                $COL_CITY_NAME TEXT PRIMARY KEY
            )
        """.trimIndent()

        db.execSQL(createWeatherTableSql)
        db.execSQL(createCityTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WEATHER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CITY")
        onCreate(db)
    }
}