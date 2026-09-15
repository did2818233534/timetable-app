package com.did2818.timetable.presentation.timetable

import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TimetableLayoutFormParserTest {
  @Test
  fun parse_acceptsSingleDigitHourAndLayoutFlags() {
    val result =
      TimetableLayoutFormParser()(
        listOf(TimetablePeriodDraft(2, "8:05", "9:40", false)),
        setOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY),
        hideEmptyDays = true,
      )

    assertEquals(LocalTime.of(8, 5), result.periods.single().startTime)
    assertEquals(2, result.periods.single().originalNumber)
    assertEquals(false, result.periods.single().visible)
    assertEquals(true, result.hideEmptyDays)
  }

  @Test
  fun parse_rejectsInvalidTimeRange() {
    assertThrows(IllegalArgumentException::class.java) {
      TimetableLayoutFormParser()(
        listOf(TimetablePeriodDraft(1, "10:00", "09:00", true)),
        setOf(DayOfWeek.MONDAY),
        false,
      )
    }
  }
}
