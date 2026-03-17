package com.example.dailyplanner.data.dao

import androidx.room.*
import com.example.dailyplanner.data.entity.PlannerEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {
    @Query("SELECT * FROM planner_events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<PlannerEventEntity>>

    @Query("SELECT * FROM planner_events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsByDate(date: Long): Flow<List<PlannerEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: PlannerEventEntity)

    @Update
    suspend fun update(event: PlannerEventEntity)

    @Delete
    suspend fun delete(event: PlannerEventEntity)

    @Query("DELETE FROM planner_events WHERE id = :id")
    suspend fun deleteById(id: Long)
}
