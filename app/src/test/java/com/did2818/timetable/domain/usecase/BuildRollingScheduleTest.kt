package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.SplitDisplaySlot
import com.did2818.timetable.domain.model.TimetableSnapshot
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildRollingScheduleTest {
  private val timetable = SampleTimetableRepository().timetable.value
  private val buildSchedule = BuildRollingSchedule()

  @Test
  fun schedule_startsOnRequestedDateAndContainsSevenDays() {
    val days = buildSchedule(LocalDate.of(2026, 9, 9), 7, timetable)

    assertEquals(LocalDate.of(2026, 9, 9), days.first().date)
    assertEquals(LocalDate.of(2026, 9, 15), days.last().date)
    assertEquals(7, days.size)
  }

  @Test
  fun schedule_evaluatesWeekPatternForEachCalendarDate() {
    val days = buildSchedule(LocalDate.of(2026, 9, 7), 7, timetable)
    val tuesdaySecondPeriod = days[1].periodItems[1].single()

    assertEquals("physics", tuesdaySecondPeriod.course.id)
    assertTrue(tuesdaySecondPeriod.isActive)

    val nextWeek = buildSchedule(LocalDate.of(2026, 9, 14), 7, timetable)
    val nextTuesdaySecondPeriod = nextWeek[1].periodItems[1].single()
    assertFalse(nextTuesdaySecondPeriod.isActive)
  }

  @Test
  fun samePeriod_showsTwoCourses() {
    val updated = timetableWithTwoCourses(splitDisplay = true)

    val courses = buildSchedule(LocalDate.of(2026, 9, 7), 7, updated)[1].periodItems[1]

    assertEquals(listOf("physics", "second"), courses.map { it.course.id })
    assertTrue(courses.all(WeekScheduleItem::isActive))
  }

  @Test
  fun samePeriod_withoutSplitConfiguration_showsOneCourse() {
    val updated = timetableWithTwoCourses(splitDisplay = false)

    val courses = buildSchedule(LocalDate.of(2026, 9, 7), 7, updated)[1].periodItems[1]

    assertEquals(listOf("physics"), courses.map { it.course.id })
  }

  private fun timetableWithTwoCourses(splitDisplay: Boolean): TimetableSnapshot {
    val second = Course("second", "第二门课程")
    val secondMeeting =
      ClassMeeting(
        id = "second-meeting",
        courseId = second.id,
        dayOfWeek = DayOfWeek.TUESDAY,
        startTime = LocalTime.of(10, 0),
        endTime = LocalTime.of(11, 35),
        weekPattern = WeekPattern(1, 18),
      )
    return timetable.copy(
      courses = timetable.courses + second,
      meetings = timetable.meetings + secondMeeting,
      splitDisplaySlots =
        if (splitDisplay) setOf(SplitDisplaySlot(DayOfWeek.TUESDAY, 2)) else emptySet(),
    )
  }
}
