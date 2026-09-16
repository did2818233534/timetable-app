package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.MainDispatcherRule
import com.did2818.timetable.data.importexport.JsonTimetableExporter
import com.did2818.timetable.data.importexport.TimetableJsonCodec
import com.did2818.timetable.data.export.spreadsheet.XlsxTimetableExporter
import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.usecase.NewTimetableSpec
import com.did2818.timetable.domain.usecase.PeriodTime
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainScreenViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun weekNavigation_updatesScheduleAndStopsAtTermBoundaries() {
    val viewModel = createViewModel()

    viewModel.selectWeek(1)
    viewModel.selectWeek(0)
    assertEquals(1, viewModel.uiState.value.selectedWeek)

    viewModel.selectWeek(-1)
    assertEquals(1, viewModel.uiState.value.selectedWeek)

    viewModel.selectWeek(18)
    viewModel.selectWeek(19)
    assertEquals(18, viewModel.uiState.value.selectedWeek)
  }

  @Test
  fun evenWeek_marksOddCourseInactiveAndEvenCourseActive() {
    val viewModel = createViewModel()
    val stateByCourse = viewModel.uiState.value.items.associateBy { it.course.id }

    assertEquals(false, stateByCourse.getValue("physics").isActive)
    assertEquals(true, stateByCourse.getValue("programming").isActive)
  }

  @Test
  fun createTimetable_retainsCurrentScheduleAndSelectsBlankStructure() {
    val viewModel = createViewModel()

    viewModel.createTimetable(
      NewTimetableSpec(
        "空白课表",
        LocalDate.of(2026, 9, 7),
        20,
        listOf(PeriodTime(LocalTime.of(8, 0), LocalTime.of(9, 30))),
      ),
    )

    assertEquals("空白课表", viewModel.uiState.value.termName)
    assertEquals(1, viewModel.uiState.value.periods.size)
    assertEquals(0, viewModel.uiState.value.items.size)
    assertEquals(2, viewModel.uiState.value.timetables.size)
  }

  @Test
  fun exportDocument_returnsImportableJson() {
    val codec = TimetableJsonCodec()
    val viewModel = createViewModel(codec)

    assertEquals("2026 秋季学期", codec.decode(viewModel.exportDocument()).term.name)
  }

  @Test
  fun exportSpreadsheetDocument_returnsXlsxPackage() {
    val viewModel = createViewModel()

    assertTrue(viewModel.exportSpreadsheetDocument().take(2).toByteArray().contentEquals("PK".toByteArray()))
  }

  private fun createViewModel(codec: TimetableJsonCodec = TimetableJsonCodec()) =
    SampleTimetableRepository().let { repository ->
      MainScreenViewModel(
        repository = repository,
        importer = TimetableImporter { repository.timetable.value },
        exporter = JsonTimetableExporter(codec),
        spreadsheetExporter = XlsxTimetableExporter(),
        initialWeek = 2,
      )
    }
}
