package com.example.cia3.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("task_id", 0)
        val title = intent.getStringExtra("task_title") ?: "Task"
        val description = intent.getStringExtra("task_description") ?: ""

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showNotification(context, taskId, title, description)
    }
}
