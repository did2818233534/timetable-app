package com.did2818.timetable.domain.model

enum class WeekParity {
  EVERY_WEEK,
  ODD_WEEKS,
  EVEN_WEEKS,
}

/** Defines which semester weeks a class meets. Week numbers are one-based. */
data class WeekPattern(
  val startWeek: Int,
  val endWeek: Int,
  val parity: WeekParity = WeekParity.EVERY_WEEK,
  val excludedWeeks: Set<Int> = emptySet(),
  val activeWeeks: Set<Int>? = null,
) {
  init {
    require(startWeek >= 1) { "Start week must be positive" }
    require(endWeek >= startWeek) { "End week cannot be before start week" }
    require(excludedWeeks.all { it >= 1 }) { "Excluded weeks must be positive" }
    require(activeWeeks?.all { it in startWeek..endWeek } != false) {
      "Explicit active weeks must be within the start and end weeks"
    }
  }

  fun includes(week: Int): Boolean {
    if (week !in startWeek..endWeek || week in excludedWeeks) return false
    activeWeeks?.let { return week in it }
    return when (parity) {
      WeekParity.EVERY_WEEK -> true
      WeekParity.ODD_WEEKS -> week % 2 == 1
      WeekParity.EVEN_WEEKS -> week % 2 == 0
    }
  }
}
