package com.example.dailyplanner.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: Long? = null,
    val targetDaysPerWeek: Int = 7,
    val createdAt: Long = System.currentTimeMillis()
)
