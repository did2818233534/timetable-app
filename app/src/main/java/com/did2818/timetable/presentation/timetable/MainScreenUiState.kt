package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.LocalDate

data class MainScreenUiState(
  val termName: String,
  val selectedWeek: Int,
  val totalWeeks: Int,
  val weekStart: LocalDate,
  val weekEnd: LocalDate,
  val periods: List<ClassPeriod>,
  val items: List<WeekScheduleItem>,
)
