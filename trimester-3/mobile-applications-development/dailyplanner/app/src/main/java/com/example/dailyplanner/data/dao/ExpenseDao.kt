package com.example.dailyplanner.data.dao

import androidx.room.*
import com.example.dailyplanner.data.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE budgetId = :budgetId ORDER BY date DESC")
    fun getExpensesByBudgetId(budgetId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE category = :category")
    fun getTotalByCategory(category: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getTotalExpenses(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)
}
