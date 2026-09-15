package com.did2818.timetable.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import com.did2818.timetable.MainActivity
import com.did2818.timetable.R
import com.did2818.timetable.domain.model.ReminderDelivery
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal class ReminderNotificationFactory(private val context: Context) {
  private val manager = context.getSystemService(NotificationManager::class.java)

  fun ensureChannels() {
    val popup =
      NotificationChannel(POPUP_CHANNEL, "课程弹窗提醒", NotificationManager.IMPORTANCE_HIGH).apply {
        description = "在课程开始前显示醒目的通知"
        enableVibration(true)
      }
    val alarm =
      NotificationChannel(ALARM_CHANNEL, "课程闹钟提醒", NotificationManager.IMPORTANCE_HIGH).apply {
        description = "在课程开始前持续响铃，直到手动关闭"
        enableVibration(true)
        setSound(null, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
      }
    manager.createNotificationChannels(listOf(popup, alarm))
  }

  fun build(payload: ReminderPayload): Notification {
    ensureChannels()
    val alarm = payload.delivery == ReminderDelivery.ALARM
    val builder =
      Notification.Builder(context, if (alarm) ALARM_CHANNEL else POPUP_CHANNEL)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("${payload.courseName} 即将上课")
        .setContentText(payload.contentText())
        .setStyle(Notification.BigTextStyle().bigText(payload.contentText()))
        .setCategory(if (alarm) Notification.CATEGORY_ALARM else Notification.CATEGORY_REMINDER)
        .setAutoCancel(!alarm)
        .setOngoing(alarm)
        .setContentIntent(contentIntent(payload))
        .addAction(Notification.Action.Builder(null, "关闭", dismissIntent(payload)).build())
    if (alarm) builder.setFullScreenIntent(alarmScreenIntent(payload), true)
    return builder.build()
  }

  private fun contentIntent(payload: ReminderPayload): PendingIntent =
    if (payload.delivery == ReminderDelivery.ALARM) alarmScreenIntent(payload)
    else {
      PendingIntent.getActivity(
        context,
        payload.notificationId,
        Intent(context, MainActivity::class.java),
        pendingFlags(),
      )
    }

  private fun alarmScreenIntent(payload: ReminderPayload): PendingIntent =
    PendingIntent.getActivity(
      context,
      payload.notificationId,
      Intent(context, ReminderAlarmActivity::class.java).putReminder(payload),
      pendingFlags(),
    )

  private fun dismissIntent(payload: ReminderPayload): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      payload.notificationId,
      Intent(context, ReminderDismissReceiver::class.java).putExtra(
        EXTRA_NOTIFICATION_ID,
        payload.notificationId,
      ),
      pendingFlags(),
    )
}

private fun ReminderPayload.contentText(): String {
  val time =
    Instant.ofEpochMilli(classStartsAtMillis)
      .atZone(ZoneId.systemDefault())
      .format(DateTimeFormatter.ofPattern("HH:mm"))
  return listOfNotNull("$time 上课", note.takeIf(String::isNotBlank)).joinToString(" · ")
}

internal const val EXTRA_NOTIFICATION_ID = "notification_id"
internal const val POPUP_CHANNEL = "course_reminder_popup_v1"
internal const val ALARM_CHANNEL = "course_reminder_alarm_v1"

private fun pendingFlags() = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
