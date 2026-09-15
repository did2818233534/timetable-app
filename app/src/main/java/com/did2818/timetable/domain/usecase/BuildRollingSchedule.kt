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
      DaySchedule(
        date = date,
        periodItems =
          timetable.periods.map { period ->
            val meeting =
              timetable.meetings.firstOrNull {
                it.dayOfWeek == date.dayOfWeek && it.startTime == period.startTime
              }
            val course = meeting?.let { coursesById[it.courseId] }
            if (meeting == null || course == null) null
            else WeekScheduleItem(course, meeting, week != null && meeting.isActiveIn(week))
          },
      )
    }
  }
}
