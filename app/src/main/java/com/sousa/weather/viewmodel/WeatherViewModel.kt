package com.sousa.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sousa.weather.model.WeatherResult
import com.sousa.weather.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    // Estado para a UI observar
    private val _weatherState = MutableStateFlow<WeatherResult?>(null)
    val weatherState: StateFlow<WeatherResult?> = _weatherState

    fun fetchWeather(city: String) {
        // SOLUÇÃO: O launch cria a coroutine necessária para chamar a função suspend
        viewModelScope.launch {
            val result = repository.getWeather(city)
            _weatherState.value = result
        }
    }
}