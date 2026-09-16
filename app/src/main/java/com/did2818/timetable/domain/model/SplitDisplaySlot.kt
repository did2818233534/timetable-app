package com.did2818.timetable.domain.model

import java.time.DayOfWeek

/** A timetable cell configured to show two courses in upper and lower halves. */
data class SplitDisplaySlot(
  val dayOfWeek: DayOfWeek,
  val periodNumber: Int,
) {
  init {
    require(periodNumber > 0) { "Period number must be positive" }
  }
}
