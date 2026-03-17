package com.example.lab9

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.*
import com.example.lab9.worker.SyncWorker
import java.util.concurrent.TimeUnit

class PortalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        schedulePeriodicSync()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SyncWorker.CHANNEL_ID,
                "Portal Sync Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for background course and announcement sync"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 30,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(SyncWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
