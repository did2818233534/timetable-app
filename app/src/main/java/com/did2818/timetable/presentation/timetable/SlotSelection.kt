package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek

internal data class SlotSelection(
  val day: DayOfWeek,
  val period: ClassPeriod,
  val items: List<WeekScheduleItem>,
)
