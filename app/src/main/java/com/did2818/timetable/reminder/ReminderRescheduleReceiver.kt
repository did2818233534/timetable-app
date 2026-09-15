package com.did2818.timetable.reminder

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.did2818.timetable.di.appContainer

class ReminderRescheduleReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action !in SUPPORTED_ACTIONS) return
    val container = context.appContainer
    container.reminderScheduler.reschedule(container.timetableRepository.timetable.value)
  }
}

private val SUPPORTED_ACTIONS =
  setOf(
    Intent.ACTION_BOOT_COMPLETED,
    Intent.ACTION_DATE_CHANGED,
    Intent.ACTION_TIME_CHANGED,
    Intent.ACTION_TIMEZONE_CHANGED,
    AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
  )
