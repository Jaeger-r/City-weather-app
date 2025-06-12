package com.example.weatherproject.models

import java.io.Serializable

data class DailyWeather(
    val day: String,
    val status: String,
    val temperatureRange: String,
    val iconDayResId: String,
    val iconNightResId: String
): Serializable