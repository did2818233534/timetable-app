package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.SplitDisplaySlot
import com.did2818.timetable.domain.model.TimetableSnapshot
import java.time.DayOfWeek

class UpdateSplitDisplaySlot {
  operator fun invoke(
    timetable: TimetableSnapshot,
    day: DayOfWeek,
    periodNumber: Int,
    enabled: Boolean,
  ): TimetableSnapshot {
    require(timetable.periods.any { it.number == periodNumber }) { "节次不存在" }
    val slot = SplitDisplaySlot(day, periodNumber)
    val updated =
      if (enabled) timetable.splitDisplaySlots + slot else timetable.splitDisplaySlots - slot
    return timetable.copy(splitDisplaySlots = updated)
  }
}
