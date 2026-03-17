package com.example.cia3.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTaskById(id: Int): Flow<Task?> = taskDao.getTaskById(id)

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteAll() {
        taskDao.deleteAllTasks()
    }
}
