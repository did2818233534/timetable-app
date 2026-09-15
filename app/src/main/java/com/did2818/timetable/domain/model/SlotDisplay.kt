package com.did2818.timetable.domain.model

data class SlotDisplay(
  val item: WeekScheduleItem,
  val hasConflict: Boolean,
)
