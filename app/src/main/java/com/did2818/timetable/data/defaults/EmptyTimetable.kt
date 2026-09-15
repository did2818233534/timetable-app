package com.did2818.timetable.data.defaults

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.TimetableSnapshot
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Unconfigured state used only until the user creates or imports a timetable. */
fun emptyTimetable(today: LocalDate = LocalDate.now()): TimetableSnapshot =
  TimetableSnapshot(
    term =
      AcademicTerm(
        id = "unconfigured",
        name = "咕嘎课程表",
        startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
        totalWeeks = 1,
      ),
    periods = emptyList(),
    courses = emptyList(),
    meetings = emptyList(),
  )
