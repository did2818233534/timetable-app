package com.did2818.timetable.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import com.did2818.timetable.MainActivity
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.usecase.BuildReminderOccurrences
import java.time.ZonedDateTime

class ReminderScheduler(
  context: Context,
  private val buildOccurrences: BuildReminderOccurrences = BuildReminderOccurrences(),
) {
  private val applicationContext = context.applicationContext
  private val alarmManager = applicationContext.getSystemService(AlarmManager::class.java)
  private val store = ReminderAlarmStore(applicationContext)

  fun reschedule(timetable: TimetableSnapshot, now: ZonedDateTime = ZonedDateTime.now()) {
    cancelScheduled()
    val occurrences = buildOccurrences(timetable, now)
    occurrences.forEach(::schedule)
    store.replace(occurrences.mapTo(linkedSetOf()) { it.alarmUri().toString() })
  }

  private fun cancelScheduled() {
    store.read().forEach { storedUri ->
      val intent = receiverIntent(storedUri.toUri())
      PendingIntent.getBroadcast(applicationContext, 0, intent, noCreateFlags())?.let {
        alarmManager.cancel(it)
        it.cancel()
      }
    }
    store.replace(emptySet())
  }

  private fun schedule(occurrence: com.did2818.timetable.domain.usecase.ReminderOccurrence) {
    val alarmIntent =
      PendingIntent.getBroadcast(
        applicationContext,
        0,
        receiverIntent(occurrence.alarmUri()).putReminder(occurrence.toPayload()),
        updateFlags(),
      )
    val triggerAt = occurrence.remindsAt.toInstant().toEpochMilli()
    if (!canScheduleExactly()) {
      alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, alarmIntent)
    } else if (occurrence.delivery == ReminderDelivery.ALARM) {
      alarmManager.setAlarmClock(
        AlarmManager.AlarmClockInfo(triggerAt, showTimetableIntent(occurrence.id)),
        alarmIntent,
      )
    } else {
      alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, alarmIntent)
    }
  }

  private fun canScheduleExactly(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

  private fun receiverIntent(uri: Uri) =
    Intent(applicationContext, ReminderAlarmReceiver::class.java).setData(uri)

  private fun showTimetableIntent(id: String): PendingIntent =
    PendingIntent.getActivity(
      applicationContext,
      id.hashCode(),
      Intent(applicationContext, MainActivity::class.java),
      updateFlags(),
    )
}

private fun com.did2818.timetable.domain.usecase.ReminderOccurrence.alarmUri(): Uri =
  Uri.Builder().scheme("guga-timetable").authority("reminder").appendPath(id).build()

private fun updateFlags() = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

private fun noCreateFlags() = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
