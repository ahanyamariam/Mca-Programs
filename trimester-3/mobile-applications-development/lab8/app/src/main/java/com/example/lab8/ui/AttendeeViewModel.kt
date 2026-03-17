package com.example.lab8.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lab8.data.Attendee
import com.example.lab8.data.AttendeeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendeeViewModel(private val repository: AttendeeRepository) : ViewModel() {

    val allAttendees: StateFlow<List<Attendee>> = repository.allAttendees.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addAttendee(name: String, age: Int, phone: String) {
        viewModelScope.launch {
            repository.insert(Attendee(name = name, age = age, phoneNumber = phone))
        }
    }

    fun updateAttendee(attendee: Attendee) {
        viewModelScope.launch {
            repository.update(attendee)
        }
    }

    fun deleteAttendee(attendee: Attendee) {
        viewModelScope.launch {
            repository.delete(attendee)
        }
    }
}

class AttendeeViewModelFactory(private val repository: AttendeeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendeeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
