package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.TimetableSnapshot
import java.time.DayOfWeek
import java.time.LocalTime

data class TimetableLayoutEdit(
  val periods: List<TimetablePeriodEdit>,
  val visibleDays: Set<DayOfWeek>,
  val hideEmptyDays: Boolean,
)

data class TimetablePeriodEdit(
  val originalNumber: Int?,
  val startTime: LocalTime,
  val endTime: LocalTime,
  val visible: Boolean,
)

class UpdateTimetableLayout {
  operator fun invoke(timetable: TimetableSnapshot, edit: TimetableLayoutEdit): TimetableSnapshot {
    require(edit.periods.isNotEmpty()) { "一天至少需要一个节次" }
    require(edit.visibleDays.isNotEmpty()) { "至少需要显示一个星期" }
    val periods = edit.periods.mapIndexed { index, item -> item.toPeriod(index + 1) }
    val meetings = timetable.meetings.map { meeting ->
      val oldPeriod =
        timetable.periods.single { it.startTime == meeting.startTime && it.endTime == meeting.endTime }
      val newIndex = edit.periods.indexOfFirst { it.originalNumber == oldPeriod.number }
      require(newIndex >= 0) { "第 ${oldPeriod.number} 节仍有课程，不能删除" }
      meeting.copy(startTime = periods[newIndex].startTime, endTime = periods[newIndex].endTime)
    }
    val splitDisplaySlots =
      timetable.splitDisplaySlots.mapNotNullTo(linkedSetOf()) { slot ->
        val newIndex = edit.periods.indexOfFirst { it.originalNumber == slot.periodNumber }
        slot.takeIf { newIndex >= 0 }?.copy(periodNumber = newIndex + 1)
      }
    return timetable.copy(
      periods = periods,
      meetings = meetings,
      visibleDays = edit.visibleDays,
      hideEmptyDays = edit.hideEmptyDays,
      splitDisplaySlots = splitDisplaySlots,
    )
  }
}

private fun TimetablePeriodEdit.toPeriod(number: Int): ClassPeriod =
  ClassPeriod(number, startTime, endTime, visible = visible)
