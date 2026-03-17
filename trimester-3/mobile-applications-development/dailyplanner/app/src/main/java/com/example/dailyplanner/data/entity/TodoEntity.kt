package com.example.dailyplanner.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Personal",
    val priority: String = "Medium",
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
