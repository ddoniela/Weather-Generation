package com.sousa.weather

import com.sousa.weather.network.WeatherApiInterface

class FakeWeatherApi(
    private val shouldFailCoordinates: Boolean = false,
    private val shouldFailWeather: Boolean = false,
    private val weatherCode: Int = 1
) : WeatherApiInterface {

    override fun getCoordinates(city: String): Pair<Double, Double>? {
        if (shouldFailCoordinates || city.isBlank() || city == "invalid") return null
        return Pair(-23.55, -46.63)
    }

    override fun getWeatherData(lat: Double, lon: Double): Pair<Double, Int>? {
        if (shouldFailWeather) return null
        return Pair(25.0, weatherCode)
    }
}