package com.did2818.timetable.widget

import com.did2818.timetable.data.sample.SampleTimetableData
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RollingWidgetScheduleTest {
  @Test
  fun schedule_startsTodayAndContainsSevenConsecutiveDaysAcrossWeekBoundary() {
    val startDate = LocalDate.of(2026, 9, 15)

    val result =
      buildRollingWidgetSchedule(
        startDate = startDate,
        term = SampleTimetableData.term,
        periods = SampleTimetableData.periods,
        courses = SampleTimetableData.courses,
        meetings = SampleTimetableData.meetings,
      )

    assertEquals(7, result.size)
    assertEquals(startDate, result.first().date)
    assertEquals(LocalDate.of(2026, 9, 21), result.last().date)
  }

  @Test
  fun schedule_recalculatesParityForEachCalendarDate() {
    val result =
      buildRollingWidgetSchedule(
        startDate = LocalDate.of(2026, 9, 15),
        term = SampleTimetableData.term,
        periods = SampleTimetableData.periods,
        courses = SampleTimetableData.courses,
        meetings = SampleTimetableData.meetings,
      )

    val tuesdayPhysics = result[0].cells[1]
    val fridayProgramming = result[3].cells[3]
    val nextMondayMath = result[6].cells[0]

    assertEquals("大学物理", tuesdayPhysics?.course?.name)
    assertFalse(tuesdayPhysics!!.isActive)
    assertTrue(fridayProgramming!!.isActive)
    assertTrue(nextMondayMath!!.isActive)
  }
}
