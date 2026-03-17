package com.example.cia3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cia3.data.Task
import com.example.cia3.data.TaskDatabase
import com.example.cia3.data.TaskRepository
import com.example.cia3.notification.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private val app = application

    val allTasks: StateFlow<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

        // Create notification channel on init
        NotificationHelper.createNotificationChannel(application)
    }

    fun insertTask(title: String, description: String, dueDate: String, dueTime: String = "") {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                dueDate = dueDate,
                dueTime = dueTime
            )
            repository.insert(task)

            // Schedule notification if date and time are set
            if (dueDate.isNotBlank() && dueTime.isNotBlank()) {
                // We need to get the ID after insert; since we use auto-generate,
                // we need to find the task. For simplicity, schedule with a hash-based ID
                val notifId = (title + dueDate + dueTime).hashCode()
                NotificationHelper.scheduleTaskAlarm(
                    context = app,
                    taskId = notifId,
                    title = title,
                    description = description,
                    dateStr = dueDate,
                    timeStr = dueTime
                )
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)

            // Cancel old alarm and schedule new one
            NotificationHelper.cancelTaskAlarm(app, task.taskId)
            if (task.dueDate.isNotBlank() && task.dueTime.isNotBlank()) {
                NotificationHelper.scheduleTaskAlarm(
                    context = app,
                    taskId = task.taskId,
                    title = task.title,
                    description = task.description,
                    dateStr = task.dueDate,
                    timeStr = task.dueTime
                )
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            NotificationHelper.cancelTaskAlarm(app, task.taskId)
            repository.delete(task)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            // Cancel all alarms for existing tasks
            allTasks.value.forEach { task ->
                NotificationHelper.cancelTaskAlarm(app, task.taskId)
            }
            repository.deleteAll()
        }
    }
}
