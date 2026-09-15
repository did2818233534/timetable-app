package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.data.sample.SampleTimetableRepository
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test

class MainScreenStateFactoryTest {
  @Test
  fun state_usesConfiguredVisiblePeriodsAndDays() {
    val source = SampleTimetableRepository().timetable.value
    val configured =
      source.copy(
        periods = source.periods.map { it.copy(visible = it.number <= 4) },
        visibleDays = DayOfWeek.entries.filterNot { it == DayOfWeek.SUNDAY }.toSet(),
      )

    val state = MainScreenStateFactory().create(configured, requestedWeek = 2)

    assertEquals(listOf(1, 2, 3, 4), state.periods.map { it.number })
    assertEquals(6, state.visibleDays.size)
    assertEquals(true, DayOfWeek.SATURDAY in state.visibleDays)
    assertEquals(false, DayOfWeek.SUNDAY in state.visibleDays)
  }
}
