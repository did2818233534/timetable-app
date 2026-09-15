package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

internal class TimetableDocumentMapper {
  fun toSnapshot(document: TimetableDocument): TimetableSnapshot {
    require(document.formatVersion == SUPPORTED_VERSION) { "不支持的格式版本" }
    require(document.periods.isNotEmpty()) { "至少需要一个节次" }
    val term = document.term.toModel()
    val periods = document.periods.map(PeriodDocument::toModel)
    val courses = document.courses.mapIndexed { index, course -> course.toModel(index) }
    val meetings = buildMeetings(document.courses, courses, periods, term.totalWeeks)
    return TimetableSnapshot(
      term,
      periods,
      courses,
      meetings,
      document.visibleDays.toVisibleDays(),
      document.hideEmptyDays,
      document.reminderSettings.toModel(),
    )
  }

  fun fromSnapshot(timetable: TimetableSnapshot): TimetableDocument =
    TimetableDocument(
      formatVersion = SUPPORTED_VERSION,
      term = timetable.term.toDocument(),
      periods = timetable.periods.map(ClassPeriod::toDocument),
      visibleDays = timetable.visibleDays.sortedBy { it.value }.map { it.name },
      hideEmptyDays = timetable.hideEmptyDays,
      reminderSettings = timetable.reminderSettings.toDocument(),
      courses = timetable.courses.map { it.toDocument(timetable) },
    )

  private fun buildMeetings(
    documents: List<CourseDocument>,
    courses: List<Course>,
    periods: List<ClassPeriod>,
    totalWeeks: Int,
  ): List<ClassMeeting> =
    documents.flatMapIndexed { courseIndex, courseDocument ->
      require(courseDocument.meetings.isNotEmpty()) { "课程“${courseDocument.name}”没有上课安排" }
      courseDocument.meetings.mapIndexed { meetingIndex, meeting ->
        meeting.toModel(courses[courseIndex].id, meetingIndex, periods, totalWeeks)
      }
    }
}

private const val SUPPORTED_VERSION = 1
