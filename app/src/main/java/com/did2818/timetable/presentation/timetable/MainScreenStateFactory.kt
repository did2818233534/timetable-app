package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.usecase.BuildWeekSchedule
import java.time.DayOfWeek

class MainScreenStateFactory(
  private val buildWeekSchedule: BuildWeekSchedule = BuildWeekSchedule(),
) {
  fun create(timetable: TimetableSnapshot, requestedWeek: Int): MainScreenUiState {
    val week = requestedWeek.coerceIn(1, timetable.term.totalWeeks)
    return MainScreenUiState(
      termName = timetable.term.name,
      selectedWeek = week,
      totalWeeks = timetable.term.totalWeeks,
      weekStart = timetable.term.dateOf(week, DayOfWeek.MONDAY),
      weekEnd = timetable.term.dateOf(week, DayOfWeek.SUNDAY),
      periods = timetable.periods.filter(ClassPeriod::visible),
      visibleDays = DayOfWeek.entries.filter(timetable.visibleDays::contains),
      items = buildWeekSchedule(week, timetable.courses, timetable.meetings),
    )
  }
}
