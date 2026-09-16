package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.DaySchedule
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.LocalDate

/** Builds consecutive calendar days and evaluates each date against its semester week. */
class BuildRollingSchedule {
  operator fun invoke(
    startDate: LocalDate,
    dayCount: Int,
    timetable: TimetableSnapshot,
  ): List<DaySchedule> {
    require(dayCount > 0) { "Day count must be positive" }
    val coursesById = timetable.courses.associateBy { it.id }

    return List(dayCount) { offset ->
      val date = startDate.plusDays(offset.toLong())
      val week = timetable.term.weekNumberOn(date)
      val displayWeek =
        week ?: if (date < timetable.term.startDate) 0 else timetable.term.totalWeeks + 1
      DaySchedule(
        date = date,
        periodItems =
          timetable.periods.filter { it.visible }.map { period ->
            val candidates =
              timetable.meetings
                .filter { it.dayOfWeek == date.dayOfWeek && it.startTime == period.startTime }
                .mapNotNull { meeting ->
                  coursesById[meeting.courseId]?.let { course ->
                    WeekScheduleItem(course, meeting, meeting.isActiveIn(displayWeek))
                  }
                }
            SelectSlotDisplays()(candidates, displayWeek, timetable.term.totalWeeks).items
          },
      )
    }
  }
}
