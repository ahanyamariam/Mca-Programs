package com.example.lab5.data.model

import com.google.gson.annotations.SerializedName

// --- Geocoding API response (geocoding-api.open-meteo.com) ---
data class GeoSearchResponse(
    @SerializedName("results") val results: List<GeocodingResult>? = null
)

data class GeocodingResult(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("country") val country: String? = null,
    @SerializedName("country_code") val countryCode: String? = null,
    @SerializedName("admin1") val admin1: String? = null,
    @SerializedName("admin2") val admin2: String? = null
)

// --- Weather API response (api.open-meteo.com) ---
data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("timezone") val timezone: String? = null,
    @SerializedName("current") val current: CurrentWeather? = null,
    @SerializedName("daily") val daily: DailyForecast? = null
)

data class CurrentWeather(
    @SerializedName("time") val time: String,
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("surface_pressure") val pressure: Double,
    @SerializedName("cloud_cover") val cloudCover: Int,
    @SerializedName("is_day") val isDay: Int? = null
)

data class DailyForecast(
    @SerializedName("time") val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>,
    @SerializedName("sunrise") val sunrise: List<String>? = null,
    @SerializedName("sunset") val sunset: List<String>? = null
)

// --- Mapped UI models ---
data class WeatherInfo(
    val description: String,
    val iconUrl: String,
    val smallIconUrl: String,
    val largeImageUrl: String
)
