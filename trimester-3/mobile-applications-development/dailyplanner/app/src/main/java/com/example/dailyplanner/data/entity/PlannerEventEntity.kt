package com.example.dailyplanner.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planner_events")
data class PlannerEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: Long,
    val startTime: String = "",
    val endTime: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "Medium",
    val createdAt: Long = System.currentTimeMillis()
)
