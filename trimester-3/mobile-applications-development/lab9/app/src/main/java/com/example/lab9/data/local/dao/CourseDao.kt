package com.example.lab9.data.local.dao

import androidx.room.*
import com.example.lab9.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY dayOfWeek, startTime")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE dayOfWeek = :day ORDER BY startTime")
    fun getCoursesByDay(day: String): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Query("DELETE FROM courses")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun count(): Int
}
