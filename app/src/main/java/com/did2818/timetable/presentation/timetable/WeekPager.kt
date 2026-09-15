package com.did2818.timetable.presentation.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.presentation.timetable.components.DayHeader
import com.did2818.timetable.presentation.timetable.components.TimetableGrid
import com.did2818.timetable.presentation.timetable.components.WeekNavigation
import java.time.DayOfWeek
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
internal fun WeekPager(
  state: MainScreenUiState,
  stateForWeek: (Int) -> MainScreenUiState,
  onSelectWeek: (Int) -> Unit,
  onOpenCell: (DayOfWeek, ClassPeriod, List<WeekScheduleItem>) -> Unit,
  onCopyItem: (WeekScheduleItem) -> Unit,
  onPasteCell: (DayOfWeek, ClassPeriod) -> Unit,
  modifier: Modifier = Modifier,
) {
  val pagerState = rememberPagerState(state.selectedWeek - 1) { state.totalWeeks }
  val scope = rememberCoroutineScope()
  val visibleWeek = stateForWeek(pagerState.currentPage + 1)

  LaunchedEffect(state.selectedWeek, state.totalWeeks) {
    val targetPage = (state.selectedWeek - 1).coerceIn(0, state.totalWeeks - 1)
    if (!pagerState.isScrollInProgress && pagerState.settledPage != targetPage) {
      pagerState.scrollToPage(targetPage)
    }
  }
  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.settledPage }
      .distinctUntilChanged()
      .drop(1)
      .collect { onSelectWeek(it + 1) }
  }

  Column(modifier) {
    WeekNavigation(
      state = visibleWeek,
      onPreviousWeek = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
      onNextWeek = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
    )
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.fillMaxSize(),
    ) { page ->
      val pageState = stateForWeek(page + 1)
      Column(Modifier.fillMaxSize().testTag("week-page-${page + 1}")) {
        DayHeader(pageState.weekStart, pageState.visibleDays)
        TimetableGrid(
          periods = pageState.periods,
          visibleDays = pageState.visibleDays,
          items = pageState.items,
          selectedWeek = pageState.selectedWeek,
          totalWeeks = pageState.totalWeeks,
          onOpenCell = onOpenCell,
          onCopyItem = onCopyItem,
          onPasteCell = onPasteCell,
          modifier = Modifier.weight(1f),
        )
      }
    }
  }
}
