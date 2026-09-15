package com.did2818.timetable.domain.model

import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassMeetingTest {
  @Test
  fun isActiveIn_delegatesToWeekPattern() {
    val meeting = meeting(WeekPattern(1, 16, WeekParity.EVEN_WEEKS))

    assertFalse(meeting.isActiveIn(5))
    assertTrue(meeting.isActiveIn(6))
  }

  @Test
  fun meeting_rejectsInvalidTimeRange() {
    assertThrows(IllegalArgumentException::class.java) {
      meeting(WeekPattern(1, 16)).copy(endTime = LocalTime.of(8, 0))
    }
  }

  private fun meeting(pattern: WeekPattern) =
    ClassMeeting(
      id = "meeting-1",
      courseId = "course-1",
      dayOfWeek = DayOfWeek.MONDAY,
      startTime = LocalTime.of(8, 0),
      endTime = LocalTime.of(9, 35),
      classroom = "A101",
      weekPattern = pattern,
    )
}
