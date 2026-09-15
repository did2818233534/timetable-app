package com.did2818.timetable.domain.model

/** A display-ready class item that remains present when inactive so the UI can gray it out. */
data class WeekScheduleItem(
  val course: Course,
  val meeting: ClassMeeting,
  val isActive: Boolean,
)
