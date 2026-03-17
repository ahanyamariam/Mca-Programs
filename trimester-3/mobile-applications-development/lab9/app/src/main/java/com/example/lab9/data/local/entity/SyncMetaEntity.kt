package com.example.lab9.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_meta")
data class SyncMetaEntity(
    @PrimaryKey val id: Int = 1,
    val lastSyncTimestamp: Long = 0L,
    val syncStatus: String = "NEVER", // "NEVER", "SUCCESS", "FAILED", "IN_PROGRESS"
    val totalCoursesSync: Int = 0,
    val totalAnnouncementsSync: Int = 0
)
