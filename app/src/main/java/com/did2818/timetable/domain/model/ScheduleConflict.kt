package com.did2818.timetable.domain.model

/** Two meetings whose time ranges overlap in one or more semester weeks. */
data class ScheduleConflict(
  val first: ClassMeeting,
  val second: ClassMeeting,
  val conflictingWeeks: Set<Int>,
)
