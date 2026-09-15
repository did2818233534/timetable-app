package com.did2818.timetable.widget

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.Course
import java.time.LocalDate

data class WidgetCourseCell(
  val course: Course,
  val meeting: ClassMeeting,
  val isActive: Boolean,
)

data class WidgetDayColumn(
  val date: LocalDate,
  val cells: List<WidgetCourseCell?>,
)

/** Builds seven consecutive calendar days beginning with [startDate]. */
fun buildRollingWidgetSchedule(
  startDate: LocalDate,
  term: AcademicTerm,
  periods: List<ClassPeriod>,
  courses: List<Course>,
  meetings: List<ClassMeeting>,
): List<WidgetDayColumn> {
  val coursesById = courses.associateBy(Course::id)
  return (0L until DAYS_TO_SHOW).map { dayOffset ->
    val date = startDate.plusDays(dayOffset)
    val week = term.weekNumberOn(date)
    WidgetDayColumn(
      date = date,
      cells =
        periods.map { period ->
          val meeting =
            meetings.firstOrNull {
              it.dayOfWeek == date.dayOfWeek && it.startTime == period.startTime
            }
          val course = meeting?.let { coursesById[it.courseId] }
          if (meeting == null || course == null) null
          else WidgetCourseCell(course, meeting, week != null && meeting.isActiveIn(week))
        },
    )
  }
}

private const val DAYS_TO_SHOW = 7L
