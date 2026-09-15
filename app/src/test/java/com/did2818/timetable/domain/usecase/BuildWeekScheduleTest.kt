package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildWeekScheduleTest {
  private val buildWeekSchedule = BuildWeekSchedule()

  @Test
  fun result_keepsInactiveClassForGrayDisplayAndSortsByDayAndTime() {
    val courses =
      listOf(
        Course(id = "math", name = "高等数学"),
        Course(id = "physics", name = "大学物理"),
      )
    val meetings =
      listOf(
        meeting("physics", DayOfWeek.TUESDAY, 10, WeekParity.EVERY_WEEK),
        meeting("math", DayOfWeek.MONDAY, 8, WeekParity.ODD_WEEKS),
      )

    val result = buildWeekSchedule(week = 2, courses = courses, meetings = meetings)

    assertEquals(listOf("math", "physics"), result.map { it.course.id })
    assertFalse(result[0].isActive)
    assertTrue(result[1].isActive)
  }

  @Test
  fun result_ignoresOrphanMeetingWithoutCourse() {
    val result =
      buildWeekSchedule(
        week = 1,
        courses = emptyList(),
        meetings = listOf(meeting("missing", DayOfWeek.MONDAY, 8, WeekParity.EVERY_WEEK)),
      )

    assertTrue(result.isEmpty())
  }

  private fun meeting(
    courseId: String,
    day: DayOfWeek,
    hour: Int,
    parity: WeekParity,
  ) =
    ClassMeeting(
      id = "$courseId-$day-$hour",
      courseId = courseId,
      dayOfWeek = day,
      startTime = LocalTime.of(hour, 0),
      endTime = LocalTime.of(hour + 1, 0),
      classroom = "A101",
      weekPattern = WeekPattern(1, 18, parity),
    )
}
