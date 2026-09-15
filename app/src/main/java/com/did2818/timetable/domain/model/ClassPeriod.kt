package com.did2818.timetable.domain.model

import java.time.LocalTime

/** A configurable row in the timetable grid. */
data class ClassPeriod(
  val number: Int,
  val startTime: LocalTime,
  val endTime: LocalTime,
  val breakAfter: String? = null,
  val visible: Boolean = true,
) {
  init {
    require(number >= 1) { "Period number must be positive" }
    require(startTime < endTime) { "Period end time must be after its start time" }
    require(breakAfter == null || breakAfter.isNotBlank()) { "Break label must not be blank" }
  }
}
