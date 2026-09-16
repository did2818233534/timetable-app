package com.did2818.timetable.presentation.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.did2818.timetable.presentation.timetable.components.EmptyTimetableScreen
import com.did2818.timetable.presentation.timetable.components.TimetableToolbar
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek

@Composable
fun MainScreen(
  state: MainScreenUiState,
  stateForWeek: (Int) -> MainScreenUiState,
  onSelectWeek: (Int) -> Unit,
  onImportClick: () -> Unit,
  onExportClick: () -> Unit,
  onNewClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onTimetableSelected: (String) -> Unit = {},
  onOpenCell: (DayOfWeek, ClassPeriod, List<WeekScheduleItem>) -> Unit,
  onCopyItem: (WeekScheduleItem) -> Unit,
  onPasteCell: (DayOfWeek, ClassPeriod) -> Unit,
  modifier: Modifier = Modifier,
) {
  if (!state.hasTimetable) {
    EmptyTimetableScreen(onImportClick = onImportClick, onNewClick = onNewClick, modifier = modifier)
    return
  }
  Column(modifier = modifier.fillMaxSize()) {
    TimetableToolbar(
      termName = state.termName,
      onImportClick = onImportClick,
      onExportClick = onExportClick,
      onNewClick = onNewClick,
      onSettingsClick = onSettingsClick,
      timetables = state.timetables,
      selectedTimetableId = state.selectedTimetableId,
      onTimetableSelected = onTimetableSelected,
    )
    WeekPager(
      state = state,
      stateForWeek = stateForWeek,
      onSelectWeek = onSelectWeek,
      onOpenCell = onOpenCell,
      onCopyItem = onCopyItem,
      onPasteCell = onPasteCell,
      modifier = Modifier.weight(1f),
    )
  }
}
