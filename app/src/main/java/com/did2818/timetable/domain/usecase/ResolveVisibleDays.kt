package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.TimetableSnapshot
import java.time.DayOfWeek

class ResolveVisibleDays {
  operator fun invoke(timetable: TimetableSnapshot): List<DayOfWeek> {
    val configured = DayOfWeek.entries.filter(timetable.visibleDays::contains)
    if (!timetable.hideEmptyDays || timetable.meetings.isEmpty()) return configured
    val daysWithCourses = timetable.meetings.mapTo(hashSetOf()) { it.dayOfWeek }
    return configured.filter(daysWithCourses::contains)
  }
}
