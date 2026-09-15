package com.did2818.timetable.presentation.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.did2818.timetable.presentation.timetable.components.DayHeader
import com.did2818.timetable.presentation.timetable.components.TimetableGrid
import com.did2818.timetable.presentation.timetable.components.WeekNavigation

@Composable
fun MainScreen(
  state: MainScreenUiState,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxSize()) {
    WeekNavigation(state, onPreviousWeek, onNextWeek)
    DayHeader(weekStart = state.weekStart)
    TimetableGrid(
      periods = state.periods,
      items = state.items,
      modifier = Modifier.weight(1f),
    )
  }
}
