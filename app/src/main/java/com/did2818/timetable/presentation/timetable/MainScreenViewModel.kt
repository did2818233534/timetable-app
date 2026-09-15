package com.did2818.timetable.presentation.timetable

import androidx.lifecycle.ViewModel
import com.did2818.timetable.data.sample.SampleTimetableData
import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.usecase.BuildWeekSchedule
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel(
  private val term: AcademicTerm = SampleTimetableData.term,
  private val courses: List<Course> = SampleTimetableData.courses,
  private val meetings: List<ClassMeeting> = SampleTimetableData.meetings,
  private val buildWeekSchedule: BuildWeekSchedule = BuildWeekSchedule(),
  clock: Clock = Clock.systemDefaultZone(),
  initialWeek: Int? = null,
) : ViewModel() {
  private val startingWeek =
    initialWeek?.coerceIn(1, term.totalWeeks)
      ?: term.weekNumberOn(LocalDate.now(clock))
      ?: 1

  private val _uiState = MutableStateFlow(stateFor(startingWeek))
  val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

  fun showPreviousWeek() = selectWeek(_uiState.value.selectedWeek - 1)

  fun showNextWeek() = selectWeek(_uiState.value.selectedWeek + 1)

  fun selectWeek(week: Int) {
    if (week !in 1..term.totalWeeks || week == _uiState.value.selectedWeek) return
    _uiState.value = stateFor(week)
  }

  private fun stateFor(week: Int) =
    MainScreenUiState(
      termName = term.name,
      selectedWeek = week,
      totalWeeks = term.totalWeeks,
      weekStart = term.dateOf(week, DayOfWeek.MONDAY),
      weekEnd = term.dateOf(week, DayOfWeek.SUNDAY),
      items = buildWeekSchedule(week, courses, meetings),
    )
}

data class MainScreenUiState(
  val termName: String,
  val selectedWeek: Int,
  val totalWeeks: Int,
  val weekStart: LocalDate,
  val weekEnd: LocalDate,
  val items: List<WeekScheduleItem>,
)
