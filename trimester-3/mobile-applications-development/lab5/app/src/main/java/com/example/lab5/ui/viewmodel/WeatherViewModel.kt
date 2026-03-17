package com.example.lab5.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab5.data.model.GeocodingResult
import com.example.lab5.data.model.WeatherCodeMapper
import com.example.lab5.data.model.WeatherResponse
import com.example.lab5.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<GeocodingResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DetailUiState(
    val weather: WeatherResponse? = null,
    val cityName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _detailState = MutableStateFlow(DetailUiState())
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

    fun updateQuery(query: String) {
        _searchState.value = _searchState.value.copy(query = query)
    }

    fun searchCity() {
        val query = _searchState.value.query.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(isLoading = true, error = null)

            val result = repository.searchCities(query)
            result.onSuccess { cities ->
                _searchState.value = _searchState.value.copy(
                    results = cities,
                    isLoading = false,
                    error = if (cities.isEmpty()) "No cities found for \"$query\"" else null
                )
            }.onFailure { exception ->
                _searchState.value = _searchState.value.copy(
                    isLoading = false,
                    error = "Search failed: ${exception.localizedMessage}"
                )
            }
        }
    }

    fun loadWeatherDetails(cityId: Long) {
        viewModelScope.launch {
            // Find city from cached search results
            val city = _searchState.value.results.find { it.id == cityId }
            if (city == null) {
                _detailState.value = _detailState.value.copy(
                    error = "City not found in search results.",
                    isLoading = false
                )
                return@launch
            }

            val displayName = buildString {
                append(city.name)
                city.admin1?.let { append(", $it") }
                city.country?.let { append(", $it") }
            }

            _detailState.value = DetailUiState(
                cityName = displayName,
                isLoading = true
            )

            val result = repository.getWeather(city.latitude, city.longitude)
            result.onSuccess { weather ->
                _detailState.value = _detailState.value.copy(
                    weather = weather,
                    isLoading = false
                )
            }.onFailure { exception ->
                _detailState.value = _detailState.value.copy(
                    error = "Failed to load weather: ${exception.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    fun clearSearch() {
        _searchState.value = SearchUiState()
    }
}
