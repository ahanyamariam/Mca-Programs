package com.example.lab9.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow
import androidx.work.*
import com.example.lab9.data.local.AppDatabase
import com.example.lab9.data.local.entity.AnnouncementEntity
import com.example.lab9.data.local.entity.CourseEntity
import com.example.lab9.data.local.entity.SyncMetaEntity
import com.example.lab9.data.repository.PortalRepository
import com.example.lab9.worker.SyncWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SyncViewModel(context: Context) : ViewModel() {

    private val db = AppDatabase.getInstance(context)
    private val repository = PortalRepository(db)
    private val workManager = WorkManager.getInstance(context)

    val courses: StateFlow<List<CourseEntity>> = repository.coursesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.announcementsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = repository.unreadCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val syncMeta: StateFlow<SyncMetaEntity?> = repository.syncMetaFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Observe the WorkManager periodic task live state
    val workerState: StateFlow<WorkInfo.State?> = workManager
        .getWorkInfosByTagLiveData(SyncWorker.WORK_NAME)
        .asFlow()
        .map { infos -> infos.firstOrNull()?.state }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun triggerManualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncNow()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun markAnnouncementRead(id: String) {
        viewModelScope.launch {
            repository.markAnnouncementRead(id)
        }
    }
}
