package com.example.endsem.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.endsem.data.model.Rental
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Rental operations
 * Implements full CRUD operations
 */
@Dao
interface RentalDao {

    // CREATE - Insert a new rental
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRental(rental: Rental)

    // READ - Get all rentals as Flow for reactive updates
    @Query("SELECT * FROM rentals ORDER BY id DESC")
    fun getAllRentals(): Flow<List<Rental>>

    // READ - Get a specific rental by movieId
    @Query("SELECT * FROM rentals WHERE movieId = :movieId LIMIT 1")
    suspend fun getRentalByMovieId(movieId: Int): Rental?

    // READ - Get all rentals (non-flow version for WorkManager)
    @Query("SELECT * FROM rentals")
    suspend fun getAllRentalsList(): List<Rental>

    // UPDATE - Update rental days
    @Query("UPDATE rentals SET days = :days WHERE id = :rentalId")
    suspend fun updateRentalDays(rentalId: Int, days: Int)

    // UPDATE - Full update
    @Update
    suspend fun updateRental(rental: Rental)

    // DELETE - Remove a rental
    @Delete
    suspend fun deleteRental(rental: Rental)

    // DELETE - Remove rental by ID
    @Query("DELETE FROM rentals WHERE id = :rentalId")
    suspend fun deleteRentalById(rentalId: Int)

    // Get total count of rentals
    @Query("SELECT COUNT(*) FROM rentals")
    suspend fun getRentalCount(): Int

    // Check if a movie is already rented
    @Query("SELECT EXISTS(SELECT 1 FROM rentals WHERE movieId = :movieId)")
    suspend fun isMovieRented(movieId: Int): Boolean
}
