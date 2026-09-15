package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.usecase.TimetableLayoutEdit
import com.did2818.timetable.domain.usecase.TimetablePeriodEdit
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TimetablePeriodDraft(
  val originalNumber: Int?,
  val startTime: String,
  val endTime: String,
  val visible: Boolean,
)

class TimetableLayoutFormParser {
  operator fun invoke(
    periods: List<TimetablePeriodDraft>,
    visibleDays: Set<DayOfWeek>,
    hideEmptyDays: Boolean,
  ): TimetableLayoutEdit {
    require(periods.isNotEmpty()) { "一天至少需要一个节次" }
    require(visibleDays.isNotEmpty()) { "至少需要显示一个星期" }
    val edits = periods.mapIndexed { index, period -> period.toEdit(index + 1) }
    require(edits.map { it.startTime to it.endTime }.distinct().size == edits.size) {
      "两个节次不能使用完全相同的时间"
    }
    return TimetableLayoutEdit(edits, visibleDays, hideEmptyDays)
  }
}

private fun TimetablePeriodDraft.toEdit(number: Int): TimetablePeriodEdit {
  val start = startTime.parseTime("第 $number 节上课时间格式不正确")
  val end = endTime.parseTime("第 $number 节下课时间格式不正确")
  require(start < end) { "第 $number 节下课时间必须晚于上课时间" }
  return TimetablePeriodEdit(originalNumber, start, end, visible)
}

private fun String.parseTime(errorMessage: String): LocalTime =
  runCatching { LocalTime.parse(trim(), TIME_FORMAT) }.getOrElse { throw IllegalArgumentException(errorMessage) }

private val TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm")
