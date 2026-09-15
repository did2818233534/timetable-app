package com.did2818.timetable.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class WeekPatternTest {
  @Test
  fun everyWeek_includesBothBoundaries() {
    val pattern = WeekPattern(startWeek = 3, endWeek = 6)

    assertFalse(pattern.includes(2))
    assertTrue(pattern.includes(3))
    assertTrue(pattern.includes(6))
    assertFalse(pattern.includes(7))
  }

  @Test
  fun oddWeeks_useAbsoluteSemesterWeekNumbers() {
    val pattern = WeekPattern(2, 7, WeekParity.ODD_WEEKS)

    assertFalse(pattern.includes(2))
    assertTrue(pattern.includes(3))
    assertFalse(pattern.includes(4))
    assertTrue(pattern.includes(7))
  }

  @Test
  fun evenWeeks_useAbsoluteSemesterWeekNumbers() {
    val pattern = WeekPattern(1, 6, WeekParity.EVEN_WEEKS)

    assertFalse(pattern.includes(1))
    assertTrue(pattern.includes(2))
    assertTrue(pattern.includes(6))
  }

  @Test
  fun excludedWeek_overridesRegularPattern() {
    val pattern = WeekPattern(1, 8, excludedWeeks = setOf(3, 4))

    assertFalse(pattern.includes(3))
    assertFalse(pattern.includes(4))
    assertTrue(pattern.includes(5))
  }

  @Test
  fun invalidRange_isRejected() {
    assertThrows(IllegalArgumentException::class.java) { WeekPattern(5, 4) }
  }
}
