package com.example.lab9.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val code: String,
    val title: String,
    val instructor: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val credits: Int,
    val color: Long = 0xFF3F51B5
)
