package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FindScheduleConflictsTest {
  private val findConflicts = FindScheduleConflicts()

  @Test
  fun overlappingMeetings_reportOnlyWeeksWhenBothAreActive() {
    val everyWeek = meeting("every", 8, 0, 9, 40, WeekPattern(1, 6))
    val evenWeeks =
      meeting(
        "even",
        9,
        0,
        10,
        30,
        WeekPattern(2, 6, WeekParity.EVEN_WEEKS, excludedWeeks = setOf(4)),
      )

    val conflicts = findConflicts(listOf(everyWeek, evenWeeks))

    assertEquals(1, conflicts.size)
    assertEquals(setOf(2, 6), conflicts.single().conflictingWeeks)
  }

  @Test
  fun oddAndEvenMeetings_doNotConflict() {
    val odd = meeting("odd", 8, 0, 10, 0, WeekPattern(1, 8, WeekParity.ODD_WEEKS))
    val even = meeting("even", 8, 0, 10, 0, WeekPattern(1, 8, WeekParity.EVEN_WEEKS))

    assertTrue(findConflicts(listOf(odd, even)).isEmpty())
  }

  @Test
  fun adjacentMeetings_doNotConflict() {
    val first = meeting("first", 8, 0, 9, 30, WeekPattern(1, 8))
    val second = meeting("second", 9, 30, 11, 0, WeekPattern(1, 8))

    assertTrue(findConflicts(listOf(first, second)).isEmpty())
  }

  @Test
  fun meetingsOnDifferentDays_doNotConflict() {
    val monday = meeting("monday", 8, 0, 10, 0, WeekPattern(1, 8))
    val tuesday = monday.copy(id = "tuesday", dayOfWeek = DayOfWeek.TUESDAY)

    assertTrue(findConflicts(listOf(monday, tuesday)).isEmpty())
  }

  private fun meeting(
    id: String,
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    pattern: WeekPattern,
  ) =
    ClassMeeting(
      id = id,
      courseId = id,
      dayOfWeek = DayOfWeek.MONDAY,
      startTime = LocalTime.of(startHour, startMinute),
      endTime = LocalTime.of(endHour, endMinute),
      weekPattern = pattern,
    )
}
