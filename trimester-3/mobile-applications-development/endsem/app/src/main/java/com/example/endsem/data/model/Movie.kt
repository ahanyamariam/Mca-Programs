package com.example.endsem.data.model

import com.google.gson.annotations.SerializedName

/**
 * Movie data class representing a movie from the API
 */
data class Movie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("poster")
    val posterUrl: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("genre")
    val genre: String = "Drama",

    @SerializedName("trailer_url")
    val trailerUrl: String? = null
) {
    val rentalPricePerDay: Double
        get() = when {
            rating >= 8.0 -> 5.99
            rating >= 7.0 -> 4.99
            rating >= 6.0 -> 3.99
            rating >= 5.0 -> 2.99
            else -> 1.99
        }
}

/**
 * API Response wrapper
 */
data class MovieResponse(
    @SerializedName("movies")
    val movies: List<Movie>? = null
)
