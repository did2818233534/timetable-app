package com.did2818.timetable.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class AcademicTermTest {
  private val term =
    AcademicTerm(
      id = "2026-autumn",
      name = "2026 秋季学期",
      startDate = LocalDate.of(2026, 9, 7),
      totalWeeks = 18,
    )

  @Test
  fun weekNumberOn_returnsOneBasedWeekInsideTerm() {
    assertEquals(1, term.weekNumberOn(LocalDate.of(2026, 9, 7)))
    assertEquals(1, term.weekNumberOn(LocalDate.of(2026, 9, 13)))
    assertEquals(2, term.weekNumberOn(LocalDate.of(2026, 9, 14)))
    assertEquals(18, term.weekNumberOn(term.endDate))
  }

  @Test
  fun weekNumberOn_returnsNullOutsideTerm() {
    assertNull(term.weekNumberOn(term.startDate.minusDays(1)))
    assertNull(term.weekNumberOn(term.endDate.plusDays(1)))
  }

  @Test
  fun dateOf_mapsWeekAndDayToCalendarDate() {
    assertEquals(LocalDate.of(2026, 9, 16), term.dateOf(2, DayOfWeek.WEDNESDAY))
  }

  @Test
  fun term_rejectsNonMondayStartDate() {
    assertThrows(IllegalArgumentException::class.java) {
      term.copy(startDate = LocalDate.of(2026, 9, 8))
    }
  }
}
