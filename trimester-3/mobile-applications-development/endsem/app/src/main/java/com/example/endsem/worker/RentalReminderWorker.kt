package com.example.endsem.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.endsem.MainActivity
import com.example.endsem.MovieExplorerApp
import com.example.endsem.R
import com.example.endsem.data.local.AppDatabase

/**
 * Worker class that runs every 5 hours to remind users about their active rentals
 * and how much time is remaining
 */
class RentalReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Get database instance
            val database = AppDatabase.getDatabase(context)
            val rentalDao = database.rentalDao()

            // Retrieve all rentals from Room database
            val rentals = rentalDao.getAllRentalsList()
            val currentTime = System.currentTimeMillis()

            // Show notification for each active rental with remaining time
            val activeRentals = rentals.filter { it.remainingTimeMillis(currentTime) > 0 }

            if (activeRentals.isNotEmpty()) {
                showRentalReminderNotification(activeRentals.size, activeRentals.map {
                    val remaining = it.remainingTimeMillis(currentTime)
                    val hours = remaining / (1000 * 60 * 60)
                    val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)
                    "${it.title}: ${hours}h ${minutes}m left"
                })
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showRentalReminderNotification(rentalCount: Int, rentalDetails: List<String>) {
        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        // Create intent to open the app when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val detailText = rentalDetails.joinToString("\n")

        // Build the notification
        val notification = NotificationCompat.Builder(context, MovieExplorerApp.RENTAL_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_movie_notification)
            .setContentTitle("Rental Time Remaining")
            .setContentText(
                if (rentalCount == 1) {
                    rentalDetails.first()
                } else {
                    "You have $rentalCount active rentals"
                }
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(detailText)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        NotificationManagerCompat.from(context).notify(
            MovieExplorerApp.RENTAL_NOTIFICATION_ID,
            notification
        )
    }
}
