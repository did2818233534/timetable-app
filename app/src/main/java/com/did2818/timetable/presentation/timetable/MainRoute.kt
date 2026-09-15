package com.did2818.timetable.presentation.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.did2818.timetable.domain.repository.TimetableRepository

@Composable
fun MainRoute(
  repository: TimetableRepository,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(repository) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  MainScreen(
    state = state,
    onPreviousWeek = viewModel::showPreviousWeek,
    onNextWeek = viewModel::showNextWeek,
    modifier = modifier,
  )
}
