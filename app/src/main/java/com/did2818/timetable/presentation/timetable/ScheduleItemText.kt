package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekScheduleItem

fun WeekScheduleItem.compactWeekLabel(): String {
  meeting.weekPattern.activeWeeks?.let { weeks ->
    return weeks.sorted().joinToString(",", postfix = "周")
  }
  val parityText =
    when (meeting.weekPattern.parity) {
      WeekParity.EVERY_WEEK -> "每周"
      WeekParity.ODD_WEEKS -> "单周"
      WeekParity.EVEN_WEEKS -> "双周"
    }
  return "${meeting.weekPattern.startWeek}–${meeting.weekPattern.endWeek}周 $parityText"
}
