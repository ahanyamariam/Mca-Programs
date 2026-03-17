package com.example.dailyplanner.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val allocatedAmount: Double,
    val category: String,
    val month: Int,
    val year: Int,
    val createdAt: Long = System.currentTimeMillis()
)
