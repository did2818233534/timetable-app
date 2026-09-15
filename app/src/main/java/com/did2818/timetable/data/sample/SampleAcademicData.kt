package com.did2818.timetable.data.sample

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassPeriod
import java.time.LocalDate
import java.time.LocalTime

internal object SampleAcademicData {
  val term =
    AcademicTerm(
      id = "2026-autumn",
      name = "2026 秋季学期",
      startDate = LocalDate.of(2026, 9, 7),
      totalWeeks = 18,
    )

  val periods =
    listOf(
      ClassPeriod(1, LocalTime.of(8, 0), LocalTime.of(9, 35)),
      ClassPeriod(2, LocalTime.of(10, 0), LocalTime.of(11, 35), breakAfter = "午休"),
      ClassPeriod(3, LocalTime.of(14, 0), LocalTime.of(15, 35)),
      ClassPeriod(4, LocalTime.of(15, 50), LocalTime.of(17, 25), breakAfter = "晚休"),
      ClassPeriod(5, LocalTime.of(19, 0), LocalTime.of(20, 35)),
    )
}
