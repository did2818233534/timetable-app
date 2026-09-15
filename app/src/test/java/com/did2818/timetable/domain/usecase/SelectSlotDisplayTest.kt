package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectSlotDisplayTest {
  private val select = SelectSlotDisplay()

  @Test
  fun activeCourses_showOneAndMarkConflict() {
    val result = select(listOf(item("a", WeekPattern(1, 8)), item("b", WeekPattern(1, 8))), 3, 18)

    assertTrue(result?.item?.isActive == true)
    assertTrue(result?.hasConflict == true)
  }

  @Test
  fun inactiveSlot_showsNearestFutureCourse() {
    val result =
      select(
        listOf(item("later", WeekPattern(8, 10)), item("next", WeekPattern(5, 6))),
        selectedWeek = 3,
        totalWeeks = 18,
      )

    assertEquals("next", result?.item?.course?.id)
    assertFalse(result?.item?.isActive == true)
    assertFalse(result?.hasConflict == true)
  }

  @Test
  fun noFutureCourse_showsMostRecentPastCourse() {
    val result =
      select(
        listOf(item("old", WeekPattern(1, 2)), item("recent", WeekPattern(4, 6))),
        selectedWeek = 10,
        totalWeeks = 18,
      )

    assertEquals("recent", result?.item?.course?.id)
    assertFalse(result?.item?.isActive == true)
  }

  private fun item(id: String, pattern: WeekPattern): WeekScheduleItem =
    WeekScheduleItem(
      course = Course(id, id),
      meeting =
        ClassMeeting(
          id = "meeting-$id",
          courseId = id,
          dayOfWeek = DayOfWeek.MONDAY,
          startTime = LocalTime.of(8, 0),
          endTime = LocalTime.of(9, 0),
          weekPattern = pattern,
        ),
      isActive = false,
    )
}
