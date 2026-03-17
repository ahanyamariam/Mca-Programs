package com.example.dailyplanner.ui.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyplanner.data.AppDatabase
import com.example.dailyplanner.data.entity.TodoEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).todoDao()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val todos: StateFlow<List<TodoEntity>> = dao.getAllTodos()
        .combine(_selectedCategory) { todos, category ->
            if (category == "All") todos
            else todos.filter { it.category == category }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addTodo(title: String, description: String, category: String, priority: String, dueDate: Long?) {
        viewModelScope.launch {
            dao.insert(
                TodoEntity(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDate = dueDate
                )
            )
        }
    }

    fun toggleComplete(todo: TodoEntity) {
        viewModelScope.launch {
            dao.update(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            dao.delete(todo)
        }
    }

    fun updateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            dao.update(todo)
        }
    }

    companion object {
        val categories = listOf("All", "Work", "Personal", "Health", "Shopping", "Other")
        val priorities = listOf("High", "Medium", "Low")
    }
}
