package com.example.endsem.data.remote

import com.example.endsem.data.model.MovieResponse
import retrofit2.http.GET

/**
 * API Service interface for movie data
 */
interface MovieApiService {

    @GET("api/movies")
    suspend fun getMovies(): MovieResponse

    companion object {
        const val BASE_URL = "https://fooapi.com/"
    }
}
