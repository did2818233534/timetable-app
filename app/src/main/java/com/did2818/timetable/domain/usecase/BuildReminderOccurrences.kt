package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderOverride
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.model.TimetableSnapshot
import java.time.LocalDate
import java.time.ZonedDateTime

data class ReminderOccurrence(
  val id: String,
  val courseName: String,
  val note: String,
  val classStartsAt: ZonedDateTime,
  val remindsAt: ZonedDateTime,
  val delivery: ReminderDelivery,
)

class BuildReminderOccurrences {
  operator fun invoke(
    timetable: TimetableSnapshot,
    now: ZonedDateTime,
    days: Long = DEFAULT_SCHEDULE_DAYS,
  ): List<ReminderOccurrence> =
    (0 until days)
      .map { now.toLocalDate().plusDays(it) }
      .flatMap { date -> occurrencesOn(date, timetable, now) }
      .sortedBy(ReminderOccurrence::remindsAt)

  private fun occurrencesOn(
    date: LocalDate,
    timetable: TimetableSnapshot,
    now: ZonedDateTime,
  ): List<ReminderOccurrence> {
    val week = timetable.term.weekNumberOn(date) ?: return emptyList()
    val meetings =
      timetable.meetings.filter { it.dayOfWeek == date.dayOfWeek && it.isActiveIn(week) }
    val firstStart = meetings.minOfOrNull { it.startTime }
    return meetings.mapNotNull { meeting ->
      val configuration =
        meeting.reminderOverride?.takeIf(ReminderOverride::enabled)?.let {
          EffectiveReminder(it.minutesBefore, it.delivery)
        } ?: if (meeting.reminderOverride != null) null
        else timetable.reminderSettings.effectiveFor(meeting.startTime == firstStart)
      configuration?.let { effective ->
        val startsAt = date.atTime(meeting.startTime).atZone(now.zone)
        val remindsAt = startsAt.minusMinutes(effective.minutesBefore.toLong())
        if (remindsAt <= now) null
        else {
          val course = timetable.courses.single { it.id == meeting.courseId }
          ReminderOccurrence(
            id = "${meeting.id}|$date",
            courseName = course.name,
            note = meeting.displayNote.ifBlank { course.note },
            classStartsAt = startsAt,
            remindsAt = remindsAt,
            delivery = effective.delivery,
          )
        }
      }
    }
  }
}

private data class EffectiveReminder(val minutesBefore: Int, val delivery: ReminderDelivery)

private fun com.did2818.timetable.domain.model.ReminderSettings.effectiveFor(
  isFirstClass: Boolean,
): EffectiveReminder? =
  when (scope) {
    ReminderScope.DISABLED -> null
    ReminderScope.ALL_CLASSES -> EffectiveReminder(minutesBefore, delivery)
    ReminderScope.FIRST_CLASS_OF_DAY ->
      if (isFirstClass) EffectiveReminder(minutesBefore, delivery) else null
  }

private const val DEFAULT_SCHEDULE_DAYS = 8L
