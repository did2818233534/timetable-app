package com.did2818.timetable.domain.model

import java.time.DayOfWeek
import java.time.LocalDateTime

data class TimetableStatistics(
  val remainingThisWeek: Int,
  val weeksUntilLastClass: Int?,
  val remainingClasses: Int,
  val completedClasses: Int,
  val remainingByDay: Map<DayOfWeek, Int>,
  val lastClassAt: LocalDateTime?,
) {
  val totalClasses: Int
    get() = remainingClasses + completedClasses
}
