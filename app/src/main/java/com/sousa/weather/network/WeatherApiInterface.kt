package com.sousa.weather.network

interface WeatherApiInterface {
    // Mantenha APENAS estas duas linhas
    suspend fun getCoordinates(city: String): Pair<Double, Double>?
    suspend fun getWeatherData(lat: Double, lon: Double): Pair<Double, Int>?
}