package com.did2818.timetable.presentation.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.did2818.timetable.domain.repository.TimetableRepository
import java.time.Clock
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainScreenViewModel(
  private val repository: TimetableRepository,
  clock: Clock = Clock.systemDefaultZone(),
  initialWeek: Int? = null,
  private val stateFactory: MainScreenStateFactory = MainScreenStateFactory(),
) : ViewModel() {
  private val initialTimetable = repository.timetable.value
  private val startingWeek =
    initialWeek?.coerceIn(1, initialTimetable.term.totalWeeks)
      ?: initialTimetable.term.weekNumberOn(LocalDate.now(clock))
      ?: 1
  private val selectedWeek = MutableStateFlow(startingWeek)

  val uiState: StateFlow<MainScreenUiState> =
    combine(repository.timetable, selectedWeek, stateFactory::create)
      .stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        stateFactory.create(initialTimetable, startingWeek),
      )

  fun showPreviousWeek() = selectWeek(selectedWeek.value - 1)

  fun showNextWeek() = selectWeek(selectedWeek.value + 1)

  fun selectWeek(week: Int) {
    if (week in 1..repository.timetable.value.term.totalWeeks) selectedWeek.value = week
  }
}
