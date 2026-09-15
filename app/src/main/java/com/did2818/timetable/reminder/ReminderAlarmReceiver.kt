package com.did2818.timetable.reminder

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.did2818.timetable.domain.model.ReminderDelivery

class ReminderAlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val payload = intent.reminderPayload() ?: return
    if (!context.canPostNotifications()) return
    val notification = ReminderNotificationFactory(context).build(payload)
    context.getSystemService(NotificationManager::class.java)
      .notify(payload.notificationId, notification)
    if (payload.delivery == ReminderDelivery.ALARM) {
      runCatching {
        context.startForegroundService(
          Intent(context, ReminderRingingService::class.java).putReminder(payload),
        )
      }
    }
  }
}

private fun Context.canPostNotifications(): Boolean =
  Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
