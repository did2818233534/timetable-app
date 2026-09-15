package com.did2818.timetable.presentation.timetable

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NewTimetableFormParserTest {
  private val parser = NewTimetableFormParser()

  @Test
  fun parse_buildsConfigurablePeriodList() {
    val result = parser("新学期", "2026-09-07", "20", "08:00-09:30\n10:15—11:45")

    assertEquals("新学期", result.name)
    assertEquals(LocalDate.of(2026, 9, 7), result.startDate)
    assertEquals(20, result.totalWeeks)
    assertEquals(LocalTime.of(10, 15), result.periods.last().start)
    assertEquals(LocalTime.of(11, 45), result.periods.last().end)
  }

  @Test
  fun parse_rejectsStartDateThatIsNotMonday() {
    assertThrows(IllegalArgumentException::class.java) {
      parser("新学期", "2026-09-08", "18", "08:00-09:30")
    }
  }
}
