package com.example.dailyplanner.ui.planner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyplanner.data.AppDatabase
import com.example.dailyplanner.data.entity.PlannerEventEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class PlannerViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).plannerDao()

    private val _selectedDate = MutableStateFlow(getTodayStart())
    val selectedDate: StateFlow<Long> = _selectedDate

    val events: StateFlow<List<PlannerEventEntity>> = _selectedDate
        .flatMapLatest { date ->
            dao.getEventsByDate(date)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(date: Long) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        _selectedDate.value = cal.timeInMillis
    }

    fun addEvent(title: String, description: String, startTime: String, endTime: String, priority: String) {
        viewModelScope.launch {
            dao.insert(
                PlannerEventEntity(
                    title = title,
                    description = description,
                    date = _selectedDate.value,
                    startTime = startTime,
                    endTime = endTime,
                    priority = priority
                )
            )
        }
    }

    fun toggleEventComplete(event: PlannerEventEntity) {
        viewModelScope.launch {
            dao.update(event.copy(isCompleted = !event.isCompleted))
        }
    }

    fun deleteEvent(event: PlannerEventEntity) {
        viewModelScope.launch {
            dao.delete(event)
        }
    }

    private fun getTodayStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    companion object {
        val priorities = listOf("High", "Medium", "Low")
    }
}
