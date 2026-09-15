package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.sample.SampleTimetableRepository
import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UpdateTimetableLayoutTest {
  private val source = SampleTimetableRepository().timetable.value

  @Test
  fun update_changesPeriodTimesAndMovesExistingMeetings() {
    val edits = source.periods.map { period ->
      TimetablePeriodEdit(
        originalNumber = period.number,
        startTime = if (period.number == 1) LocalTime.of(8, 10) else period.startTime,
        endTime = if (period.number == 1) LocalTime.of(9, 45) else period.endTime,
        visible = period.number <= 4,
      )
    }

    val updated =
      UpdateTimetableLayout()(source, TimetableLayoutEdit(edits, setOf(DayOfWeek.MONDAY), true))

    assertEquals(LocalTime.of(8, 10), updated.periods.first().startTime)
    assertEquals(LocalTime.of(8, 10), updated.meetings.single { it.id == "math-1" }.startTime)
    assertEquals(false, updated.periods.last().visible)
    assertEquals(setOf(DayOfWeek.MONDAY), updated.visibleDays)
    assertEquals(true, updated.hideEmptyDays)
  }

  @Test
  fun deletingPeriodUsedByCourse_isRejected() {
    val edits = source.periods.drop(1).map { period ->
      TimetablePeriodEdit(period.number, period.startTime, period.endTime, period.visible)
    }

    val error = assertThrows(IllegalArgumentException::class.java) {
      UpdateTimetableLayout()(source, TimetableLayoutEdit(edits, source.visibleDays, false))
    }

    assertEquals("第 1 节仍有课程，不能删除", error.message)
  }
}
