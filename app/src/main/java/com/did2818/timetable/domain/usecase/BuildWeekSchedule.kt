package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.WeekScheduleItem

/** Builds one week's timetable, retaining inactive classes for gray display. */
class BuildWeekSchedule {
  operator fun invoke(
    week: Int,
    courses: List<Course>,
    meetings: List<ClassMeeting>,
  ): List<WeekScheduleItem> {
    require(week >= 1) { "Week must be positive" }
    val coursesById = courses.associateBy(Course::id)

    return meetings
      .mapNotNull { meeting ->
        coursesById[meeting.courseId]?.let { course ->
          WeekScheduleItem(
            course = course,
            meeting = meeting,
            isActive = meeting.isActiveIn(week),
          )
        }
      }
      .sortedWith(
        compareBy<WeekScheduleItem>(
          { it.meeting.dayOfWeek.value },
          { it.meeting.startTime },
          { it.course.name },
        ),
      )
  }
}
