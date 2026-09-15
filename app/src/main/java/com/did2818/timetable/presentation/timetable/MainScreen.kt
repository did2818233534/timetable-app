package com.did2818.timetable.presentation.timetable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
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
  onOpenCell: (DayOfWeek, ClassPeriod, List<WeekScheduleItem>) -> Unit,
  onCopyItem: (WeekScheduleItem) -> Unit,
  onPasteCell: (DayOfWeek, ClassPeriod) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxSize()) {
    TimetableToolbar(state.termName, onImportClick)
    WeekNavigation(state, onPreviousWeek, onNextWeek)
    AnimatedContent(
      targetState = state,
      transitionSpec = {
        val direction = if (targetState.selectedWeek > initialState.selectedWeek) 1 else -1
        (slideInHorizontally(tween(260)) { direction * it } + fadeIn(tween(180))) togetherWith
          (slideOutHorizontally(tween(260)) { -direction * it } + fadeOut(tween(180)))
      },
      contentKey = MainScreenUiState::selectedWeek,
      modifier =
        Modifier
          .weight(1f)
          .weekSwipeGesture(onSwipeLeft = onNextWeek, onSwipeRight = onPreviousWeek),
    ) { visibleState ->
      Column {
        DayHeader(visibleState.weekStart, visibleState.visibleDays)
        TimetableGrid(
          periods = visibleState.periods,
          visibleDays = visibleState.visibleDays,
          items = visibleState.items,
          selectedWeek = visibleState.selectedWeek,
          totalWeeks = visibleState.totalWeeks,
          onOpenCell = onOpenCell,
          onCopyItem = onCopyItem,
          onPasteCell = onPasteCell,
          modifier = Modifier.weight(1f),
        )
      }
    }
  }
}
