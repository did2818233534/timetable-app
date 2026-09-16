package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.model.ReminderSettings
import com.did2818.timetable.domain.model.SplitDisplaySlot
import com.did2818.timetable.domain.model.TimetableSummary
import java.time.DayOfWeek
import java.time.LocalDate

data class MainScreenUiState(
  val hasTimetable: Boolean,
  val termName: String,
  val selectedWeek: Int,
  val totalWeeks: Int,
  val weekStart: LocalDate,
  val weekEnd: LocalDate,
  val allPeriods: List<ClassPeriod>,
  val periods: List<ClassPeriod>,
  val configuredDays: Set<DayOfWeek>,
  val visibleDays: List<DayOfWeek>,
  val hideEmptyDays: Boolean,
  val reminderSettings: ReminderSettings,
  val splitDisplaySlots: Set<SplitDisplaySlot>,
  val items: List<WeekScheduleItem>,
  val timetables: List<TimetableSummary> = emptyList(),
  val selectedTimetableId: String? = null,
)
