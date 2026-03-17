package com.example.dailyplanner.ui.habit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyplanner.data.AppDatabase
import com.example.dailyplanner.data.entity.HabitEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).habitDao()

    val habits: StateFlow<List<HabitEntity>> = dao.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addHabit(name: String, description: String, targetDaysPerWeek: Int) {
        viewModelScope.launch {
            dao.insert(
                HabitEntity(
                    name = name,
                    description = description,
                    targetDaysPerWeek = targetDaysPerWeek
                )
            )
        }
    }

    fun checkInHabit(habit: HabitEntity) {
        viewModelScope.launch {
            val today = getTodayStart()
            val yesterday = today - 86400000L

            val lastCompleted = habit.lastCompletedDate ?: 0L

            if (lastCompleted >= today) return@launch

            val newStreak = if (lastCompleted >= yesterday) {
                habit.currentStreak + 1
            } else {
                1
            }
            val newBest = maxOf(habit.bestStreak, newStreak)

            dao.update(
                habit.copy(
                    currentStreak = newStreak,
                    bestStreak = newBest,
                    lastCompletedDate = today
                )
            )
        }
    }

    fun isCompletedToday(habit: HabitEntity): Boolean {
        val today = getTodayStart()
        return (habit.lastCompletedDate ?: 0L) >= today
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            dao.delete(habit)
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
}
