package com.did2818.timetable.reminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderDismissReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
    if (notificationId != 0) {
      context.getSystemService(NotificationManager::class.java).cancel(notificationId)
    }
    context.stopService(Intent(context, ReminderRingingService::class.java))
  }
}
