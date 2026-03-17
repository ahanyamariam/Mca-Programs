package com.example.lab9.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lab9.R
import com.example.lab9.data.local.AppDatabase
import com.example.lab9.data.repository.PortalRepository

class SyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "PortalSync"
        const val CHANNEL_ID = "portal_sync_channel"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(context)
            val repository = PortalRepository(db)

            val success = repository.syncNow()

            if (success) {
                val meta = db.syncMetaDao().getSyncMetaOnce()
                showSyncNotification(
                    context,
                    "✅ Sync Complete",
                    "Fetched ${meta?.totalCoursesSync ?: 0} courses & ${meta?.totalAnnouncementsSync ?: 0} announcements."
                )
                Result.success()
            } else {
                showSyncNotification(
                    context,
                    "⚠️ Sync Failed",
                    "Could not fetch latest data. Will retry in 6 hours."
                )
                Result.retry()
            }
        } catch (e: Exception) {
            showSyncNotification(
                context,
                "⚠️ Sync Error",
                "An error occurred during sync: ${e.localizedMessage}"
            )
            Result.failure()
        }
    }

    private fun showSyncNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Portal Sync",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for background data sync"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
