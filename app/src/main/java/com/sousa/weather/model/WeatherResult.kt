package com.sousa.weather.model

sealed class WeatherResult {
    data class Success(
        val city: String,
        val temperature: Double,
        val description: String
    ) : WeatherResult()

    data class Error(
        val message: String
    ) : WeatherResult()
}