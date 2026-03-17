package com.example.dailyplanner.data.dao

import androidx.room.*
import com.example.dailyplanner.data.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE category = :category ORDER BY isCompleted ASC, createdAt DESC")
    fun getTodosByCategory(category: String): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteById(id: Long)
}
