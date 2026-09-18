package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.defaults.emptyTimetable
import com.did2818.timetable.data.sample.SampleTimetableRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculateTimetableStatisticsTest {
  private val calculate = CalculateTimetableStatistics()

  @Test
  fun countsOccurrencesUsingWeekPatternsAndCurrentTime() {
    val timetable = SampleTimetableRepository().timetable.value
    val result = calculate(timetable, LocalDateTime.of(2026, 9, 15, 12, 0))

    assertEquals(2, result.remainingThisWeek)
    assertEquals(16, result.weeksUntilLastClass)
    assertEquals(50, result.remainingClasses)
    assertEquals(4, result.completedClasses)
    assertEquals(1, result.remainingByDay.getValue(DayOfWeek.WEDNESDAY))
    assertEquals(1, result.remainingByDay.getValue(DayOfWeek.FRIDAY))
    assertEquals(0, result.remainingByDay.getValue(DayOfWeek.MONDAY))
  }

  @Test
  fun afterTermAllOccurrencesAreCompleted() {
    val timetable = SampleTimetableRepository().timetable.value
    val result = calculate(timetable, LocalDateTime.of(2027, 1, 31, 12, 0))

    assertEquals(0, result.remainingThisWeek)
    assertEquals(0, result.remainingClasses)
    assertEquals(54, result.completedClasses)
    assertNull(result.weeksUntilLastClass)
    assertNull(result.lastClassAt)
  }

  @Test
  fun emptyTimetableReturnsZeroesForEveryDay() {
    val result =
      calculate(emptyTimetable(LocalDate.of(2026, 9, 14)), LocalDateTime.of(2026, 9, 15, 12, 0))

    assertEquals(0, result.totalClasses)
    assertEquals(DayOfWeek.entries.associateWith { 0 }, result.remainingByDay)
  }
}
