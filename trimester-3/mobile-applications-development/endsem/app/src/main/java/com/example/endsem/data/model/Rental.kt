package com.example.endsem.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rental Entity for Room Database
 * Stores information about rented movies
 */
@Entity(tableName = "rentals")
data class Rental(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val movieId: Int,

    val title: String,

    val posterUrl: String,

    val rating: Double,

    val days: Int = 1,

    val pricePerDay: Double,

    val overview: String = "",

    val rentalStartTime: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = days * pricePerDay

    /**
     * Returns the expiry time in millis (rentalStartTime + days * 24 hours)
     */
    val expiryTimeMillis: Long
        get() = rentalStartTime + (days.toLong() * 24 * 60 * 60 * 1000)

    /**
     * Returns remaining time in milliseconds, or 0 if expired
     */
    fun remainingTimeMillis(currentTime: Long = System.currentTimeMillis()): Long {
        val remaining = expiryTimeMillis - currentTime
        return if (remaining > 0) remaining else 0
    }
}
