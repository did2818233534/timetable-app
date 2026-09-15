package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.defaults.emptyTimetable
import com.did2818.timetable.data.sample.SampleTimetableRepository
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test

class ResolveVisibleDaysTest {
  @Test
  fun autoHide_keepsOnlyConfiguredDaysThatContainCourses() {
    val source = SampleTimetableRepository().timetable.value

    val days = ResolveVisibleDays()(source.copy(hideEmptyDays = true))

    assertEquals(
      listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
      days,
    )
  }

  @Test
  fun blankTimetable_keepsConfiguredDaysSoCoursesCanBeAdded() {
    val source = emptyTimetable().copy(visibleDays = setOf(DayOfWeek.MONDAY), hideEmptyDays = true)

    assertEquals(listOf(DayOfWeek.MONDAY), ResolveVisibleDays()(source))
  }
}
