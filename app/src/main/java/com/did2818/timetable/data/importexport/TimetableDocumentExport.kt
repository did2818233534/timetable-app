package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.TimetableSnapshot

internal fun AcademicTerm.toDocument() = TermDocument(name, startDate.toString(), totalWeeks)

internal fun ClassPeriod.toDocument() =
  PeriodDocument(number, startTime.toString(), endTime.toString(), breakAfter)

internal fun Course.toDocument(timetable: TimetableSnapshot) =
  CourseDocument(
    name = name,
    teacher = teacher,
    color = "#${colorArgb.toString(16).uppercase().padStart(8, '0')}",
    note = note,
    meetings =
      timetable.meetings.filter { it.courseId == id }.map { meeting ->
        val period =
          timetable.periods.single {
            it.startTime == meeting.startTime && it.endTime == meeting.endTime
          }
        MeetingDocument(
          day = meeting.dayOfWeek.name,
          period = period.number,
          classroom = meeting.classroom,
          startWeek = meeting.weekPattern.startWeek,
          endWeek = meeting.weekPattern.endWeek,
          parity = meeting.weekPattern.parity.name,
          excludedWeeks = meeting.weekPattern.excludedWeeks,
        )
      },
  )
