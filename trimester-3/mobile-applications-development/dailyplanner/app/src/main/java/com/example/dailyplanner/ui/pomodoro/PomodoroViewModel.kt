package com.example.dailyplanner.ui.pomodoro

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeLeft = MutableStateFlow(25 * 60 * 1000L)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentMode = MutableStateFlow(PomodoroMode.WORK)
    val currentMode: StateFlow<PomodoroMode> = _currentMode

    private val _sessionsCompleted = MutableStateFlow(0)
    val sessionsCompleted: StateFlow<Int> = _sessionsCompleted

    private val _totalDuration = MutableStateFlow(25 * 60 * 1000L)
    val totalDuration: StateFlow<Long> = _totalDuration

    private var timer: CountDownTimer? = null

    fun startPause() {
        if (_isRunning.value) {
            pause()
        } else {
            start()
        }
    }

    private fun start() {
        _isRunning.value = true
        timer = object : CountDownTimer(_timeLeft.value, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished
            }

            override fun onFinish() {
                _timeLeft.value = 0
                _isRunning.value = false
                onSessionComplete()
            }
        }.start()
    }

    private fun pause() {
        timer?.cancel()
        _isRunning.value = false
    }

    fun reset() {
        timer?.cancel()
        _isRunning.value = false
        _timeLeft.value = getDurationForMode(_currentMode.value)
        _totalDuration.value = _timeLeft.value
    }

    private fun onSessionComplete() {
        when (_currentMode.value) {
            PomodoroMode.WORK -> {
                _sessionsCompleted.value++
                if (_sessionsCompleted.value % 4 == 0) {
                    switchMode(PomodoroMode.LONG_BREAK)
                } else {
                    switchMode(PomodoroMode.SHORT_BREAK)
                }
            }
            PomodoroMode.SHORT_BREAK, PomodoroMode.LONG_BREAK -> {
                switchMode(PomodoroMode.WORK)
            }
        }
    }

    fun switchMode(mode: PomodoroMode) {
        timer?.cancel()
        _isRunning.value = false
        _currentMode.value = mode
        _timeLeft.value = getDurationForMode(mode)
        _totalDuration.value = _timeLeft.value
    }

    private fun getDurationForMode(mode: PomodoroMode): Long {
        return when (mode) {
            PomodoroMode.WORK -> 25 * 60 * 1000L
            PomodoroMode.SHORT_BREAK -> 5 * 60 * 1000L
            PomodoroMode.LONG_BREAK -> 15 * 60 * 1000L
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}

enum class PomodoroMode(val label: String) {
    WORK("Focus"),
    SHORT_BREAK("Short Break"),
    LONG_BREAK("Long Break")
}
