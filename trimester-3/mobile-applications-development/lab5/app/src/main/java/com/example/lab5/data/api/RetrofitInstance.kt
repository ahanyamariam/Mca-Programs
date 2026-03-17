package com.example.lab5.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val geocodingRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val weatherRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geocodingApi: GeocodingApiService by lazy {
        geocodingRetrofit.create(GeocodingApiService::class.java)
    }

    val weatherApi: WeatherApiService by lazy {
        weatherRetrofit.create(WeatherApiService::class.java)
    }
}
