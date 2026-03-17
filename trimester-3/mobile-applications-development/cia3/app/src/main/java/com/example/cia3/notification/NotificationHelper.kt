package com.example.cia3.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.cia3.MainActivity
import com.example.cia3.R
import android.app.AlarmManager
import java.util.Calendar

object NotificationHelper {

    private const val CHANNEL_ID = "task_reminder_channel"
    private const val CHANNEL_NAME = "Task Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task reminders"
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, taskId: Int, title: String, description: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, taskId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Starting: $title")
            .setContentText(description.ifBlank { "Your task is starting now!" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(taskId, notification)
    }

    fun scheduleTaskAlarm(
        context: Context,
        taskId: Int,
        title: String,
        description: String,
        dateStr: String, // dd/MM/yyyy
        timeStr: String  // HH:mm
    ) {
        if (dateStr.isBlank() || timeStr.isBlank()) return

        try {
            val dateParts = dateStr.split("/")
            val timeParts = timeStr.split(":")
            if (dateParts.size != 3 || timeParts.size != 2) return

            val day = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1 // Calendar months are 0-based
            val year = dateParts[2].toInt()
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Don't schedule if in the past
            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
                putExtra("task_id", taskId)
                putExtra("task_title", title)
                putExtra("task_description", description)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context, taskId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    // Fall back to inexact alarm
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: Exception) {
            // Silently fail if date/time parsing fails
        }
    }

    fun cancelTaskAlarm(context: Context, taskId: Int) {
        val intent = Intent(context, TaskAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, taskId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
