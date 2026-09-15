package com.did2818.timetable.domain.usecase

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateEmptyTimetableTest {
  @Test
  fun create_hasStructureButNoCourses() {
    val timetable =
      CreateEmptyTimetable()(
        NewTimetableSpec(
          name = "我的课表",
          startDate = LocalDate.of(2026, 9, 7),
          totalWeeks = 18,
          periods =
            listOf(
              PeriodTime(LocalTime.of(8, 0), LocalTime.of(9, 30)),
              PeriodTime(LocalTime.of(10, 0), LocalTime.of(11, 30)),
            ),
        ),
      )

    assertEquals(listOf(1, 2), timetable.periods.map { it.number })
    assertTrue(timetable.courses.isEmpty())
    assertTrue(timetable.meetings.isEmpty())
  }
}
