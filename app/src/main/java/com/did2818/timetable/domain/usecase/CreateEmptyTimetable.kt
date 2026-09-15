package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.TimetableSnapshot
import java.time.LocalDate
import java.time.LocalTime

data class NewTimetableSpec(
  val name: String,
  val startDate: LocalDate,
  val totalWeeks: Int,
  val periods: List<PeriodTime>,
)

data class PeriodTime(
  val start: LocalTime,
  val end: LocalTime,
)

class CreateEmptyTimetable {
  operator fun invoke(spec: NewTimetableSpec): TimetableSnapshot {
    require(spec.name.isNotBlank()) { "课表名称不能为空" }
    require(spec.periods.isNotEmpty()) { "至少需要一个节次" }
    return TimetableSnapshot(
      term =
        AcademicTerm(
          id = "created-${spec.startDate}",
          name = spec.name.trim(),
          startDate = spec.startDate,
          totalWeeks = spec.totalWeeks,
        ),
      periods =
        spec.periods.mapIndexed { index, period ->
          ClassPeriod(index + 1, period.start, period.end)
        },
      courses = emptyList(),
      meetings = emptyList(),
    )
  }
}
