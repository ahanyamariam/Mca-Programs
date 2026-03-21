package com.example.endsem.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.endsem.data.model.WishlistItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Wishlist operations
 */
@Dao
interface WishlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItem)

    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    fun getAllWishlistItems(): Flow<List<WishlistItem>>

    @Query("SELECT * FROM wishlist")
    suspend fun getAllWishlistItemsList(): List<WishlistItem>

    @Delete
    suspend fun removeFromWishlist(item: WishlistItem)

    @Query("DELETE FROM wishlist WHERE movieId = :movieId")
    suspend fun removeByMovieId(movieId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE movieId = :movieId)")
    suspend fun isInWishlist(movieId: Int): Boolean

    @Query("SELECT COUNT(*) FROM wishlist")
    suspend fun getWishlistCount(): Int
}
