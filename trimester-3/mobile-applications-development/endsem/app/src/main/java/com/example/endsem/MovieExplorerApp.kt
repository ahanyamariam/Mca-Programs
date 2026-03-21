package com.example.endsem

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.endsem.data.local.AppDatabase
import com.example.endsem.data.remote.RetrofitInstance
import com.example.endsem.repository.MovieRepository
import com.example.endsem.worker.RentalReminderWorker
import java.util.concurrent.TimeUnit

/**
 * Application class for Movie Explorer
 */
class MovieExplorerApp : Application() {

    // Lazy initialization of database
    val database by lazy { AppDatabase.getDatabase(this) }

    // Lazy initialization of repository
    val repository by lazy {
        MovieRepository(
            RetrofitInstance.movieApiService,
            database.rentalDao(),
            database.wishlistDao()
        )
    }

    override fun onCreate() {
        super.onCreate()

        // Create notification channels
        createNotificationChannels()

        // Schedule periodic rental reminder work (every 5 hours)
        scheduleRentalReminder()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Rental reminder channel
            val reminderChannel = NotificationChannel(
                RENTAL_REMINDER_CHANNEL_ID,
                "Rental Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Periodic reminders about your active movie rentals and time remaining"
            }

            // Purchase success channel
            val purchaseChannel = NotificationChannel(
                PURCHASE_CHANNEL_ID,
                "Purchase Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you successfully purchase a movie rental"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(purchaseChannel)
        }
    }

    private fun scheduleRentalReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<RentalReminderWorker>(
            5, TimeUnit.HOURS // Every 5 hours
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RENTAL_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }

    companion object {
        const val RENTAL_REMINDER_CHANNEL_ID = "rental_reminders"
        const val PURCHASE_CHANNEL_ID = "purchase_notifications"
        const val RENTAL_REMINDER_WORK_NAME = "rental_reminder_work"
        const val RENTAL_NOTIFICATION_ID = 1001
    }
}
