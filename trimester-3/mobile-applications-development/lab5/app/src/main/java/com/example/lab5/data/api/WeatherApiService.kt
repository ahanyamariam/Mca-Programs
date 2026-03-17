package com.example.lab5.data.api

import com.example.lab5.data.model.GeoSearchResponse
import com.example.lab5.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {

    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") query: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeoSearchResponse
}

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,surface_pressure,cloud_cover,is_day",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponse
}
