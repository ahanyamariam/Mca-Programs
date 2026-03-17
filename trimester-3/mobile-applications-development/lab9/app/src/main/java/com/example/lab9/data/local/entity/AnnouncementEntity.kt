package com.example.lab9.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val courseCode: String,
    val courseTitle: String,
    val title: String,
    val body: String,
    val postedAt: Long,
    val isRead: Boolean = false,
    val priority: String = "NORMAL" // "HIGH", "NORMAL", "LOW"
)
