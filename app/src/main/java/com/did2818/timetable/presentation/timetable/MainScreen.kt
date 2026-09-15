package com.did2818.timetable.presentation.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.did2818.timetable.presentation.timetable.components.DayHeader
import com.did2818.timetable.presentation.timetable.components.TimetableGrid
import com.did2818.timetable.presentation.timetable.components.TimetableToolbar
import com.did2818.timetable.presentation.timetable.components.WeekNavigation
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek

@Composable
fun MainScreen(
  state: MainScreenUiState,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
  onImportClick: () -> Unit,
  onEditItem: (WeekScheduleItem) -> Unit,
  onCopyItem: (WeekScheduleItem) -> Unit,
  onCreateCell: (DayOfWeek, ClassPeriod) -> Unit,
  onPasteCell: (DayOfWeek, ClassPeriod) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxSize()) {
    TimetableToolbar(state.termName, onImportClick)
    WeekNavigation(state, onPreviousWeek, onNextWeek)
    DayHeader(weekStart = state.weekStart)
    TimetableGrid(
      periods = state.periods,
      items = state.items,
      onEditItem = onEditItem,
      onCopyItem = onCopyItem,
      onCreateCell = onCreateCell,
      onPasteCell = onPasteCell,
      modifier =
        Modifier
          .weight(1f)
          .weekSwipeGesture(onSwipeLeft = onNextWeek, onSwipeRight = onPreviousWeek),
    )
  }
}
