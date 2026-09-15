package com.did2818.timetable.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

/** One recurring time and classroom arrangement for a course. */
data class ClassMeeting(
  val id: String,
  val courseId: String,
  val dayOfWeek: DayOfWeek,
  val startTime: LocalTime,
  val endTime: LocalTime,
  val classroom: String = "",
  val weekPattern: WeekPattern,
) {
  init {
    require(id.isNotBlank()) { "Meeting id cannot be blank" }
    require(courseId.isNotBlank()) { "Course id cannot be blank" }
    require(startTime < endTime) { "Class end time must be after its start time" }
  }

  fun isActiveIn(week: Int): Boolean = weekPattern.includes(week)
}
