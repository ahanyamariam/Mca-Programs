package com.example.lab8.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendeeDao {
    @Query("SELECT * FROM attendees ORDER BY name ASC")
    fun getAllAttendees(): Flow<List<Attendee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendee(attendee: Attendee)

    @Update
    suspend fun updateAttendee(attendee: Attendee)

    @Delete
    suspend fun deleteAttendee(attendee: Attendee)
}
