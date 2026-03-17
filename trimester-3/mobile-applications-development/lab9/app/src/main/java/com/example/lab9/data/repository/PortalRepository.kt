package com.example.lab9.data.repository

import com.example.lab9.data.local.AppDatabase
import com.example.lab9.data.local.entity.SyncMetaEntity
import com.example.lab9.data.remote.MockApiService
import kotlinx.coroutines.flow.Flow

class PortalRepository(private val db: AppDatabase) {

    val coursesFlow = db.courseDao().getAllCourses()
    val announcementsFlow = db.announcementDao().getAllAnnouncements()
    val unreadCountFlow: Flow<Int> = db.announcementDao().getUnreadCount()
    val syncMetaFlow = db.syncMetaDao().getSyncMeta()

    suspend fun syncNow(): Boolean {
        return try {
            // Mark sync as in progress
            db.syncMetaDao().upsert(
                SyncMetaEntity(
                    lastSyncTimestamp = System.currentTimeMillis(),
                    syncStatus = "IN_PROGRESS"
                )
            )

            // Fetch from mock API
            val courses = MockApiService.fetchCourses()
            val announcements = MockApiService.fetchAnnouncements()

            // Save to Room DB
            db.courseDao().deleteAll()
            db.courseDao().insertAll(courses)
            db.announcementDao().deleteAll()
            db.announcementDao().insertAll(announcements)

            // Update sync meta with success
            db.syncMetaDao().upsert(
                SyncMetaEntity(
                    lastSyncTimestamp = System.currentTimeMillis(),
                    syncStatus = "SUCCESS",
                    totalCoursesSync = courses.size,
                    totalAnnouncementsSync = announcements.size
                )
            )
            true
        } catch (e: Exception) {
            db.syncMetaDao().upsert(
                SyncMetaEntity(
                    lastSyncTimestamp = System.currentTimeMillis(),
                    syncStatus = "FAILED"
                )
            )
            false
        }
    }

    suspend fun markAnnouncementRead(id: String) {
        db.announcementDao().markAsRead(id)
    }
}
