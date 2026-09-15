package com.did2818.timetable.presentation.timetable

import org.junit.Assert.assertEquals
import org.junit.Test

class MainScreenViewModelTest {
  @Test
  fun weekNavigation_updatesScheduleAndStopsAtTermBoundaries() {
    val viewModel = MainScreenViewModel(initialWeek = 2)

    viewModel.showPreviousWeek()
    assertEquals(1, viewModel.uiState.value.selectedWeek)

    viewModel.showPreviousWeek()
    assertEquals(1, viewModel.uiState.value.selectedWeek)

    viewModel.selectWeek(18)
    viewModel.showNextWeek()
    assertEquals(18, viewModel.uiState.value.selectedWeek)
  }

  @Test
  fun evenWeek_marksOddCourseInactiveAndEvenCourseActive() {
    val viewModel = MainScreenViewModel(initialWeek = 2)
    val stateByCourse = viewModel.uiState.value.items.associateBy { it.course.id }

    assertEquals(false, stateByCourse.getValue("physics").isActive)
    assertEquals(true, stateByCourse.getValue("programming").isActive)
  }
}
