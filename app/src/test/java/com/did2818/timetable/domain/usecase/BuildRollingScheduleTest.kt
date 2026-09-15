package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.sample.SampleTimetableRepository
import java.time.LocalDate
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
    val tuesdaySecondPeriod = days[1].periodItems[1]

    assertEquals("physics", tuesdaySecondPeriod?.course?.id)
    assertTrue(tuesdaySecondPeriod?.isActive == true)

    val nextWeek = buildSchedule(LocalDate.of(2026, 9, 14), 7, timetable)
    val nextTuesdaySecondPeriod = nextWeek[1].periodItems[1]
    assertFalse(nextTuesdaySecondPeriod?.isActive == true)
  }
}
