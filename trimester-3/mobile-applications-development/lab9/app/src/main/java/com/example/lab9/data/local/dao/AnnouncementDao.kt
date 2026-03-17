package com.example.lab9.data.local.dao

import androidx.room.*
import com.example.lab9.data.local.entity.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY postedAt DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT COUNT(*) FROM announcements WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(announcements: List<AnnouncementEntity>)

    @Query("UPDATE announcements SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("DELETE FROM announcements")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM announcements")
    suspend fun count(): Int
}
