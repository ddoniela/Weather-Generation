package com.sousa.weather.network

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class WeatherApi : WeatherApiInterface {

    private val client = OkHttpClient()

    override suspend fun getCoordinates(city: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        // ⚠️ IMPORTANTE: Precisamos codificar o nome da cidade (ex: "São Paulo" -> "S%C3%A3o+Paulo")
        val encodedCity = URLEncoder.encode(city, "UTF-8")
        val url = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=1&language=pt&format=json"

        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)

            // O Open-Meteo retorna a lista dentro de "results"
            val results = json.optJSONArray("results")
            if (results != null && results.length() > 0) {
                val firstResult = results.getJSONObject(0)
                Pair(firstResult.getDouble("latitude"), firstResult.getDouble("longitude"))
            } else {
                null // Aqui é onde caía antes se a URL estivesse errada
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getWeatherData(lat: Double, lon: Double): Pair<Double, Int>? = withContext(Dispatchers.IO) {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"

        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)
            val current = json.getJSONObject("current_weather")

            Pair(current.getDouble("temperature"), current.getInt("weathercode"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}