package com.example.lab5.data.repository

import com.example.lab5.data.api.RetrofitInstance
import com.example.lab5.data.model.GeocodingResult
import com.example.lab5.data.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {

    private val geocodingApi = RetrofitInstance.geocodingApi
    private val weatherApi = RetrofitInstance.weatherApi

    suspend fun searchCities(query: String): Result<List<GeocodingResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = geocodingApi.searchCities(query)
                Result.success(response.results ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val result = weatherApi.getWeather(lat, lon)
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
