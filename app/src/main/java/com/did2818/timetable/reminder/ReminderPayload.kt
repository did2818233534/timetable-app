package com.did2818.timetable.reminder

import android.content.Intent
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.usecase.ReminderOccurrence

data class ReminderPayload(
  val id: String,
  val courseName: String,
  val note: String,
  val classStartsAtMillis: Long,
  val delivery: ReminderDelivery,
) {
  val notificationId: Int
    get() = (id.hashCode() and Int.MAX_VALUE).coerceAtLeast(1)
}

internal fun ReminderOccurrence.toPayload() =
  ReminderPayload(id, courseName, note, classStartsAt.toInstant().toEpochMilli(), delivery)

internal fun Intent.putReminder(payload: ReminderPayload): Intent =
  putExtra(EXTRA_ID, payload.id)
    .putExtra(EXTRA_COURSE, payload.courseName)
    .putExtra(EXTRA_NOTE, payload.note)
    .putExtra(EXTRA_START, payload.classStartsAtMillis)
    .putExtra(EXTRA_DELIVERY, payload.delivery.name)

internal fun Intent.reminderPayload(): ReminderPayload? {
  val id = getStringExtra(EXTRA_ID) ?: return null
  val course = getStringExtra(EXTRA_COURSE) ?: return null
  val delivery =
    runCatching { ReminderDelivery.valueOf(getStringExtra(EXTRA_DELIVERY).orEmpty()) }.getOrNull()
      ?: return null
  return ReminderPayload(id, course, getStringExtra(EXTRA_NOTE).orEmpty(), getLongExtra(EXTRA_START, 0), delivery)
}

private const val EXTRA_ID = "reminder_id"
private const val EXTRA_COURSE = "reminder_course"
private const val EXTRA_NOTE = "reminder_note"
private const val EXTRA_START = "reminder_start"
private const val EXTRA_DELIVERY = "reminder_delivery"
