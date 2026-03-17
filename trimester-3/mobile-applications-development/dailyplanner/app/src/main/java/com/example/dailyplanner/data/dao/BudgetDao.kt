package com.example.dailyplanner.data.dao

import androidx.room.*
import com.example.dailyplanner.data.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year ORDER BY category ASC")
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Long): BudgetEntity?

    @Query("SELECT COALESCE(SUM(allocatedAmount), 0.0) FROM budgets")
    fun getTotalAllocated(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity)

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
