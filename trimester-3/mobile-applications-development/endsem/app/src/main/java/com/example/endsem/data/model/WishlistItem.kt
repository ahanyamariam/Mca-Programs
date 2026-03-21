package com.example.endsem.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WishlistItem Entity for Room Database
 * Stores movies bookmarked by the user
 */
@Entity(tableName = "wishlist")
data class WishlistItem(
    @PrimaryKey
    val movieId: Int,
    val title: String,
    val posterUrl: String,
    val overview: String,
    val rating: Double,
    val genre: String = "Drama",
    val trailerUrl: String? = null,
    val pricePerDay: Double,
    val addedAt: Long = System.currentTimeMillis()
)
