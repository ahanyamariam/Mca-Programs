package com.example.cia3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cia3.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = SettingsDataStore(application)

    val darkMode: StateFlow<Int> = dataStore.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val accentColor: StateFlow<Int> = dataStore.accentColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val fontSize: StateFlow<Int> = dataStore.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val sortOrder: StateFlow<Int> = dataStore.sortOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val viewFilter: StateFlow<Int> = dataStore.viewFilter
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val confirmDelete: StateFlow<Boolean> = dataStore.confirmDelete
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userName: StateFlow<String> = dataStore.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val profileImageUri: StateFlow<String> = dataStore.profileImageUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val appLockEnabled: StateFlow<Boolean> = dataStore.appLockEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val hideDescriptions: StateFlow<Boolean> = dataStore.hideDescriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setDarkMode(value: Int) = viewModelScope.launch { dataStore.setDarkMode(value) }
    fun setAccentColor(value: Int) = viewModelScope.launch { dataStore.setAccentColor(value) }
    fun setFontSize(value: Int) = viewModelScope.launch { dataStore.setFontSize(value) }

    fun setSortOrder(value: Int) = viewModelScope.launch { dataStore.setSortOrder(value) }
    fun setViewFilter(value: Int) = viewModelScope.launch { dataStore.setViewFilter(value) }
    fun setConfirmDelete(value: Boolean) = viewModelScope.launch { dataStore.setConfirmDelete(value) }

    fun setUserName(value: String) = viewModelScope.launch { dataStore.setUserName(value) }
    fun setProfileImageUri(value: String) = viewModelScope.launch { dataStore.setProfileImageUri(value) }

    fun setAppLockEnabled(value: Boolean) = viewModelScope.launch { dataStore.setAppLockEnabled(value) }
    fun setHideDescriptions(value: Boolean) = viewModelScope.launch { dataStore.setHideDescriptions(value) }
}
