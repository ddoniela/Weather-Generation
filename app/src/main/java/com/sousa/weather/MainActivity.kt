package com.sousa.weather

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sousa.weather.model.WeatherResult
import com.sousa.weather.network.WeatherApi
import com.sousa.weather.repository.WeatherRepository
import com.sousa.weather.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Instância manual (Para projetos simples sem Hilt/Koin)
    private val viewModel = WeatherViewModel(
        WeatherRepository(WeatherApi())
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editCity = findViewById<EditText>(R.id.editCity)
        val button = findViewById<Button>(R.id.buttonSearch)
        val resultText = findViewById<TextView>(R.id.textResult)

        // 1. ESCUTAR o ViewModel
        // Sempre que o ViewModel mudar o estado, a UI se atualiza sozinha
        lifecycleScope.launch {
            viewModel.weatherState.collect { result ->
                // Dentro do seu collect { result -> ... }
                when (result) {
                    is WeatherResult.Success -> {
                        findViewById<TextView>(R.id.textResult).text = "${result.temperature}°C"
                        findViewById<TextView>(R.id.textDescription).text = result.description
                    }
                    is WeatherResult.Error -> {
                        findViewById<TextView>(R.id.textResult).text = "Erro"
                        findViewById<TextView>(R.id.textDescription).text = result.message
                    }
                    null -> { /* Estado inicial */ }
                }
            }
        }

        // 2. DISPARAR a busca
        button.setOnClickListener {
            val city = editCity.text.toString()
            if (city.isNotEmpty()) {
                resultText.text = "Buscando..."
                viewModel.fetchWeather(city) // Não precisa de Thread manual aqui!
            }
        }
    }
}