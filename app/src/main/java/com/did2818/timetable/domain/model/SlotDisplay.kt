package com.did2818.timetable.domain.model

data class SlotDisplay(
  val item: WeekScheduleItem,
  val hasConflict: Boolean,
)

data class SlotDisplayGroup(
  val items: List<WeekScheduleItem>,
  val hasConflict: Boolean,
)
