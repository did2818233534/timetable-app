package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.usecase.BuildWeekSchedule
import com.did2818.timetable.domain.usecase.ResolveVisibleDays
import java.time.DayOfWeek

class MainScreenStateFactory(
  private val buildWeekSchedule: BuildWeekSchedule = BuildWeekSchedule(),
  private val resolveVisibleDays: ResolveVisibleDays = ResolveVisibleDays(),
) {
  fun create(timetable: TimetableSnapshot, requestedWeek: Int): MainScreenUiState {
    val week = requestedWeek.coerceIn(1, timetable.term.totalWeeks)
    return MainScreenUiState(
      hasTimetable = timetable.periods.isNotEmpty(),
      termName = timetable.term.name,
      selectedWeek = week,
      totalWeeks = timetable.term.totalWeeks,
      weekStart = timetable.term.dateOf(week, DayOfWeek.MONDAY),
      weekEnd = timetable.term.dateOf(week, DayOfWeek.SUNDAY),
      allPeriods = timetable.periods,
      periods = timetable.periods.filter(ClassPeriod::visible),
      configuredDays = timetable.visibleDays,
      visibleDays = resolveVisibleDays(timetable),
      hideEmptyDays = timetable.hideEmptyDays,
      items = buildWeekSchedule(week, timetable.courses, timetable.meetings),
    )
  }
}
