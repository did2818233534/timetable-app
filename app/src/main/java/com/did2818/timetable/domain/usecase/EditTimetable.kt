package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek
import java.util.UUID

class EditTimetable(
  private val newId: () -> String = { UUID.randomUUID().toString() },
) {
  fun add(
    timetable: TimetableSnapshot,
    day: DayOfWeek,
    periodNumber: Int,
    edit: ClassEdit,
  ): TimetableSnapshot {
    val period = timetable.periods.single { it.number == periodNumber }
    validateEdit(edit, timetable)
    val course = Course("course-${newId()}", edit.name.trim(), note = edit.note.trim())
    val meeting =
      ClassMeeting(
        id = "meeting-${newId()}",
        courseId = course.id,
        dayOfWeek = day,
        startTime = period.startTime,
        endTime = period.endTime,
        note = edit.note.trim(),
        weekPattern = edit.weekPattern,
        reminderOverride = edit.reminderOverride,
      )
    return timetable.copy(courses = timetable.courses + course, meetings = timetable.meetings + meeting)
  }

  fun update(
    timetable: TimetableSnapshot,
    meetingId: String,
    edit: ClassEdit,
  ): TimetableSnapshot {
    validateEdit(edit, timetable)
    val meeting = timetable.meetings.single { it.id == meetingId }
    val courses = timetable.courses.map { course ->
      if (course.id == meeting.courseId) {
        course.copy(name = edit.name.trim(), note = edit.note.trim())
      } else course
    }
    val meetings = timetable.meetings.map { current ->
      if (current.id == meetingId) {
        current.copy(
          note = edit.note.trim(),
          weekPattern = edit.weekPattern,
          reminderOverride = edit.reminderOverride,
        )
      } else current
    }
    return timetable.copy(courses = courses, meetings = meetings)
  }

  fun paste(
    timetable: TimetableSnapshot,
    copied: WeekScheduleItem,
    day: DayOfWeek,
    periodNumber: Int,
  ): TimetableSnapshot {
    val period = timetable.periods.single { it.number == periodNumber }
    require(copied.meeting.weekPattern.endWeek <= timetable.term.totalWeeks) {
      "复制课程的周数超过当前学期"
    }
    val sourceCourse = timetable.courses.singleOrNull { it.id == copied.course.id }
    val course = sourceCourse ?: copied.course.copy(id = "course-${newId()}")
    val meeting =
      copied.meeting.copy(
        id = "meeting-${newId()}",
        courseId = course.id,
        dayOfWeek = day,
        startTime = period.startTime,
        endTime = period.endTime,
      )
    val courses = if (sourceCourse == null) timetable.courses + course else timetable.courses
    return timetable.copy(courses = courses, meetings = timetable.meetings + meeting)
  }

  fun delete(timetable: TimetableSnapshot, meetingId: String): TimetableSnapshot {
    val meeting = timetable.meetings.single { it.id == meetingId }
    val meetings = timetable.meetings.filterNot { it.id == meetingId }
    val courses =
      if (meetings.none { it.courseId == meeting.courseId }) {
        timetable.courses.filterNot { it.id == meeting.courseId }
      } else timetable.courses
    return timetable.copy(courses = courses, meetings = meetings)
  }

  private fun validateEdit(edit: ClassEdit, timetable: TimetableSnapshot) {
    require(edit.name.isNotBlank()) { "课程名称不能为空" }
    require(edit.weekPattern.endWeek <= timetable.term.totalWeeks) { "课程周数不能超过学期总周数" }
  }

}
