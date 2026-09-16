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
  private val selectMany = SelectSlotDisplays()

  @Test
  fun activeCourses_showOneAndMarkConflict() {
    val result = select(listOf(item("a", WeekPattern(1, 8)), item("b", WeekPattern(1, 8))), 3, 18)

    assertTrue(result?.item?.isActive == true)
    assertTrue(result?.hasConflict == true)
  }

  @Test
  fun twoActiveCourses_areBothSelectedForSplitDisplay() {
    val result =
      selectMany(listOf(item("a", WeekPattern(1, 8)), item("b", WeekPattern(1, 8))), 3, 18)

    assertEquals(listOf("a", "b"), result.items.map { it.course.id })
    assertTrue(result.items.all(WeekScheduleItem::isActive))
    assertTrue(result.hasConflict)
  }

  @Test
  fun activeCourseAndNextInactiveCourse_areSelectedTogether() {
    val result =
      selectMany(
        listOf(item("active", WeekPattern(1, 8)), item("future", WeekPattern(5, 8))),
        selectedWeek = 3,
        totalWeeks = 18,
      )

    assertEquals(listOf("active", "future"), result.items.map { it.course.id })
    assertEquals(listOf(true, false), result.items.map { it.isActive })
  }

  @Test
  fun moreThanTwoCourses_selectsTwoAndKeepsPriorityOrder() {
    val result =
      selectMany(
        listOf(
          item("past", WeekPattern(1, 1)),
          item("far", WeekPattern(8, 9)),
          item("near", WeekPattern(5, 6)),
        ),
        selectedWeek = 3,
        totalWeeks = 18,
      )

    assertEquals(listOf("near", "far"), result.items.map { it.course.id })
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
