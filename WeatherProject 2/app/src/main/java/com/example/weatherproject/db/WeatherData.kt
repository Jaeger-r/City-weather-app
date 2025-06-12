package com.example.weatherproject.db

data class WeatherData(
    val city: String,
    val date: String,
    val condition: String,
    val temperature: String,
    val airQuality: String,
    val windDirection: String,
    val windPower: String
)
