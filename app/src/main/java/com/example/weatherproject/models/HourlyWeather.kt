package com.example.weatherproject.models

import java.io.Serializable

data class HourlyWeather(
    val time: String,
    val status: String,
    val temperature: String
): Serializable