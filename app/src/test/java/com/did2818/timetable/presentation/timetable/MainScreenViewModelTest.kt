package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.MainDispatcherRule
import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.repository.TimetableImporter
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainScreenViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun weekNavigation_updatesScheduleAndStopsAtTermBoundaries() {
    val viewModel = createViewModel()

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
    val viewModel = createViewModel()
    val stateByCourse = viewModel.uiState.value.items.associateBy { it.course.id }

    assertEquals(false, stateByCourse.getValue("physics").isActive)
    assertEquals(true, stateByCourse.getValue("programming").isActive)
  }

  private fun createViewModel() =
    SampleTimetableRepository().let { repository ->
      MainScreenViewModel(
        repository = repository,
        importer = TimetableImporter { repository.timetable.value },
        initialWeek = 2,
      )
    }
}
