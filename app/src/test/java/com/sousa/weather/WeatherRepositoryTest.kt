import com.sousa.weather.FakeWeatherApi
import com.sousa.weather.model.WeatherResult
import com.sousa.weather.repository.WeatherRepository
import org.junit.Assert.*
import org.junit.Test

class WeatherRepositoryTest {

    // ✅ 1. Fluxo feliz
    @Test
    fun `deve retornar sucesso quando cidade valida`() {
        val repo = WeatherRepository(FakeWeatherApi())

        val result = repo.getWeather("São Paulo")

        assertTrue(result is WeatherResult.Success)

        val success = result as WeatherResult.Success
        assertEquals("São Paulo", success.city)
        assertEquals(25.0, success.temperature, 0.0)
        assertNotNull(success.description)
    }

    // ❌ 2. Cidade inválida
    @Test
    fun `deve retornar erro quando cidade nao encontrada`() {
        val repo = WeatherRepository(
            FakeWeatherApi(shouldFailCoordinates = true)
        )

        val result = repo.getWeather("invalid")

        assertTrue(result is WeatherResult.Error)

        val error = result as WeatherResult.Error
        assertEquals("Cidade não encontrada", error.message)
    }

    // ⚠️ 3. Cidade vazia
    @Test
    fun `deve retornar erro quando cidade vazia`() {
        val repo = WeatherRepository(FakeWeatherApi())

        val result = repo.getWeather("")

        assertTrue(result is WeatherResult.Error)

        val error = result as WeatherResult.Error
        assertEquals("Cidade não encontrada", error.message)
    }

    // ❌ 4. Falha ao buscar clima
    @Test
    fun `deve retornar erro quando api de clima falha`() {
        val repo = WeatherRepository(
            FakeWeatherApi(shouldFailWeather = true)
        )

        val result = repo.getWeather("São Paulo")

        assertTrue(result is WeatherResult.Error)

        val error = result as WeatherResult.Error
        assertEquals("Erro ao buscar clima", error.message)
    }

    // 🌡️ 5. Temperatura correta
    @Test
    fun `deve retornar temperatura correta`() {
        val repo = WeatherRepository(FakeWeatherApi())

        val result = repo.getWeather("São Paulo")

        assertTrue(result is WeatherResult.Success)

        val success = result as WeatherResult.Success
        assertEquals(25.0, success.temperature, 0.0)
    }

    // 🌫️ 6. WeatherCode desconhecido
    @Test
    fun `deve retornar descricao desconhecida`() {
        val repo = WeatherRepository(
            FakeWeatherApi(weatherCode = 999)
        )

        val result = repo.getWeather("São Paulo")

        assertTrue(result is WeatherResult.Success)

        val success = result as WeatherResult.Success
        assertEquals("Desconhecido (999)", success.description)
    }

    // 🔤 7. Cidade com acento
    @Test
    fun `deve funcionar com cidade com acento`() {
        val repo = WeatherRepository(FakeWeatherApi())

        val result = repo.getWeather("São Paulo")

        assertTrue(result is WeatherResult.Success)
    }

    // 🧱 8. Estrutura correta no sucesso
    @Test
    fun `deve conter todos os dados no sucesso`() {
        val repo = WeatherRepository(FakeWeatherApi())

        val result = repo.getWeather("São Paulo")

        assertTrue(result is WeatherResult.Success)

        val success = result as WeatherResult.Success
        assertNotNull(success.city)
        assertNotNull(success.description)
    }

    @Test
    fun `deve funcionar com cidade com espacos`() {
        val repo = WeatherRepository(FakeWeatherApi())

        val result = repo.getWeather("   São Paulo   ")

        assertTrue(result is WeatherResult.Success)
    }

    @Test
    fun `deve retornar erro quando cidade so tem espacos`() {
        val repo = WeatherRepository(FakeWeatherApi())

        val result = repo.getWeather("   ")

        assertTrue(result is WeatherResult.Error)
    }


}