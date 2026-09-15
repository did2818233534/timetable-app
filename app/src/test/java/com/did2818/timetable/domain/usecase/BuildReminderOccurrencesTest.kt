package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderOverride
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.model.ReminderSettings
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class BuildReminderOccurrencesTest {
  private val source = SampleTimetableRepository().timetable.value
  private val zone = ZoneId.of("Asia/Shanghai")
  private val mondayMorning = ZonedDateTime.of(2026, 9, 7, 7, 0, 0, 0, zone)

  @Test
  fun disabledGlobalPolicy_createsNoReminders() {
    assertEquals(emptyList<ReminderOccurrence>(), BuildReminderOccurrences()(source, mondayMorning))
  }

  @Test
  fun allClasses_usesGlobalMinutesAndDelivery() {
    val timetable =
      source.copy(
        reminderSettings = ReminderSettings(ReminderScope.ALL_CLASSES, 15, ReminderDelivery.POPUP),
      )

    val reminders = BuildReminderOccurrences()(timetable, mondayMorning, days = 2)

    assertEquals(listOf("高等数学", "大学物理"), reminders.map { it.courseName })
    assertEquals(LocalTime.of(7, 45), reminders.first().remindsAt.toLocalTime())
  }

  @Test
  fun enabledMeetingOverride_worksWhileGlobalPolicyIsDisabled() {
    val meeting =
      source.meetings.first().copy(
        reminderOverride = ReminderOverride(true, 30, ReminderDelivery.ALARM),
      )
    val timetable = source.copy(meetings = listOf(meeting))

    val reminder = BuildReminderOccurrences()(timetable, mondayMorning, days = 1).single()

    assertEquals(LocalTime.of(7, 30), reminder.remindsAt.toLocalTime())
    assertEquals(ReminderDelivery.ALARM, reminder.delivery)
  }

  @Test
  fun disabledMeetingOverride_suppressesGlobalReminder() {
    val meetings =
      source.meetings.mapIndexed { index, meeting ->
        if (index == 0) meeting.copy(reminderOverride = ReminderOverride(false)) else meeting
      }
    val timetable =
      source.copy(
        meetings = meetings,
        reminderSettings = ReminderSettings(ReminderScope.ALL_CLASSES),
      )

    val reminders = BuildReminderOccurrences()(timetable, mondayMorning, days = 1)

    assertEquals(emptyList<ReminderOccurrence>(), reminders)
  }

  @Test
  fun firstClassPolicy_ignoresLaterClassButHonorsItsOverride() {
    val first = source.meetings.first()
    val later =
      source.meetings[1].copy(
        id = "later",
        dayOfWeek = DayOfWeek.MONDAY,
        reminderOverride = ReminderOverride(true, 5, ReminderDelivery.ALARM),
      )
    val timetable =
      source.copy(
        meetings = listOf(first, later),
        reminderSettings = ReminderSettings(ReminderScope.FIRST_CLASS_OF_DAY, 10),
      )

    val reminders = BuildReminderOccurrences()(timetable, mondayMorning, days = 1)

    assertEquals(listOf(LocalTime.of(7, 50), LocalTime.of(9, 55)), reminders.map { it.remindsAt.toLocalTime() })
  }

  @Test
  fun reminderAtOrBeforeNow_isSkipped() {
    val timetable =
      source.copy(
        reminderSettings = ReminderSettings(ReminderScope.ALL_CLASSES, 15),
      )
    val now = mondayMorning.withHour(7).withMinute(45)

    assertEquals(emptyList<ReminderOccurrence>(), BuildReminderOccurrences()(timetable, now, days = 1))
  }

  @Test
  fun weekPattern_filtersInactiveOddWeekCourse() {
    val timetable =
      source.copy(
        reminderSettings = ReminderSettings(ReminderScope.ALL_CLASSES),
      )
    val secondMonday = mondayMorning.plusWeeks(1)

    val reminders = BuildReminderOccurrences()(timetable, secondMonday, days = 2)

    assertEquals(listOf("高等数学"), reminders.map { it.courseName })
  }

  @Test
  fun reminderCanFallOnPreviousCalendarDay() {
    val meeting =
      source.meetings.first().copy(
        dayOfWeek = DayOfWeek.TUESDAY,
        startTime = LocalTime.of(0, 20),
        endTime = LocalTime.of(1, 0),
        reminderOverride = ReminderOverride(true, 60),
      )
    val timetable = source.copy(meetings = listOf(meeting))
    val mondayNight = mondayMorning.withHour(22)

    val reminder = BuildReminderOccurrences()(timetable, mondayNight, days = 2).single()

    assertEquals(DayOfWeek.MONDAY, reminder.remindsAt.dayOfWeek)
    assertEquals(LocalTime.of(23, 20), reminder.remindsAt.toLocalTime())
  }
}
