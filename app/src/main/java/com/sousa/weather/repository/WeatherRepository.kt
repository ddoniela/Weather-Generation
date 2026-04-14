package com.sousa.weather.repository

import com.sousa.weather.model.WeatherResult
import com.sousa.weather.network.WeatherApiInterface
import java.io.IOException
import java.util.concurrent.TimeUnit

// 1. Modelo para envolver o resultado de sucesso com um carimbo de tempo
private data class CachedWeather(
    val data: WeatherResult.Success,
    val timestamp: Long
)

class WeatherRepository(
    private val api: WeatherApiInterface
) {

    // 2. Armazenamento em memória (Chave: nome da cidade formatado)
    private val cache = mutableMapOf<String, CachedWeather>()

    // 3. Definição do tempo de expiração (1 hora em milissegundos)
    private val CACHE_EXPIRATION_MS = TimeUnit.HOURS.toMillis(1)

    /**
    //     * Obtém os dados meteorológicos atuais para uma cidade específica.
    //     *
    //     * O processo ocorre em três etapas:
    //     * 1. Validação do nome da cidade.
    //     * 2. Conversão do nome da cidade em coordenadas geográficas (Latitude/Longitude).
    //     * 3. Busca dos dados climáticos baseados nas coordenadas obtidas.
    //     *
    //     * @param city O nome da cidade para a qual o clima deve ser buscado (ex: "São Paulo").
    //     *             Espaços em branco no início ou fim serão removidos automaticamente.
    //     *
    //     * @return [WeatherResult] Um objeto selado representando o resultado da operação:
    //     *         - [WeatherResult.Success]: Contém os dados climáticos mapeados.
    //     *         - [WeatherResult.Error]: Contém uma mensagem descritiva sobre o motivo da falha.
    //     *
    //     * @throws Exception Embora a função capture a maioria dos erros internos, exceções
    //     *                   inesperadas da camada de rede podem ser propagadas dependendo
    //     *                   da implementação da [WeatherApiInterface].
    //     *
    //     * ### Exemplo de uso:
    //     * ```kotlin
    //     * val repository = WeatherRepository(apiService)
    //     *
    //     * lifecycleScope.launch {
    //     *     val result = repository.getWeather("Rio de Janeiro")
    //     *
    //     *     when (result) {
    //     *         is WeatherResult.Success -> {
    //     *             println("Cidade: ${result.city}, Temp: ${result.temperature}°C")
    //     *         }
    //     *         is WeatherResult.Error -> {
    //     *             showErrorMessage(result.message)
    //     *         }
    //     *     }
    //     * }
    //     * ```
    //     */
    suspend fun getWeather(city: String): WeatherResult {
        // Validação básica
        if (city.isBlank()) {
            return WeatherResult.Error("O nome da cidade não pode estar vazio")
        }

        val cityKey = city.trim().lowercase()
        val currentTime = System.currentTimeMillis()

        // 4. Verificação de Cache: existe e ainda é válido?
        val cachedEntry = cache[cityKey]
        if (cachedEntry != null && (currentTime - cachedEntry.timestamp) < CACHE_EXPIRATION_MS) {
            return cachedEntry.data
        }

        // 5. Se não houver cache válido, prossegue com a busca na rede
        return try {
            // Busca coordenadas
            val coordinates = api.getCoordinates(city.trim())
                ?: return WeatherResult.Error("Cidade '$city' não encontrada")

            val (lat, lon) = coordinates

            // Busca clima
            val weatherData = api.getWeatherData(lat, lon)
                ?: return WeatherResult.Error("Dados meteorológicos indisponíveis para esta região")

            val (temp, code) = weatherData

            val successResult = WeatherResult.Success(
                city = city.trim(),
                temperature = temp,
                description = mapWeatherCodeToDescription(code)
            )

            // 6. Atualiza o cache com o novo resultado de sucesso
            cache[cityKey] = CachedWeather(
                data = successResult,
                timestamp = currentTime
            )

            successResult

        } catch (e: IOException) {
            WeatherResult.Error("Falha de conexão. Verifique sua internet.")
        } catch (e: Exception) {
            WeatherResult.Error("Ocorreu um erro inesperado: ${e.localizedMessage}")
        }
    }

    private fun mapWeatherCodeToDescription(code: Int): String {
        return when (code) {
            0 -> "Céu limpo"
            1 -> "Principalmente limpo"
            2 -> "Parcialmente nublado"
            3 -> "Nublado"
            45, 48 -> "Nevoeiro"
            51, 53, 55 -> "Drizzle (Garoa)"
            61, 63, 65 -> "Chuva"
            else -> "Condição desconhecida ($code)"
        }
    }

    /**
     * Limpa o cache de uma cidade específica ou todo o cache.
     */
    fun invalidateCache(city: String? = null) {
        if (city != null) {
            cache.remove(city.trim().lowercase())
        } else {
            cache.clear()
        }
    }
}
