package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.MAX_REMINDER_MINUTES
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderOverride
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.usecase.ClassEdit

internal enum class MeetingReminderMode {
  INHERIT,
  ENABLED,
  DISABLED,
}

internal data class CourseEditorDraft(
  val name: String,
  val note: String,
  val startWeek: String,
  val endWeek: String,
  val parity: WeekParity,
  val activeWeeks: String,
  val reminderMode: MeetingReminderMode,
  val reminderMinutes: String,
  val reminderDelivery: ReminderDelivery,
) {
  val customWeeksValid: Boolean
    get() = activeWeeks.isBlank() || activeWeeks.toWeekSet() != null

  fun toEdit(totalWeeks: Int): ClassEdit {
    val first = requireNotNull(startWeek.toIntOrNull()) { "开始周格式不正确" }
    val last = requireNotNull(endWeek.toIntOrNull()) { "结束周格式不正确" }
    val weeks = activeWeeks.toWeekSet()
    require(name.isNotBlank()) { "课程名称不能为空" }
    require(first in 1..totalWeeks && last in first..totalWeeks) { "课程周数无效" }
    require(customWeeksValid && weeks?.all { it in first..last } != false) { "指定周次无效" }
    return ClassEdit(
      name,
      note,
      WeekPattern(first, last, parity, activeWeeks = weeks),
      reminderOverride(),
    )
  }

  private fun reminderOverride(): ReminderOverride? {
    val minutes = reminderMinutes.toIntOrNull()
    return when (reminderMode) {
      MeetingReminderMode.INHERIT -> null
      MeetingReminderMode.DISABLED -> ReminderOverride(false, minutes ?: 10, reminderDelivery)
      MeetingReminderMode.ENABLED -> {
        require(minutes != null && minutes in 0..MAX_REMINDER_MINUTES) { "提醒分钟数无效" }
        ReminderOverride(true, minutes, reminderDelivery)
      }
    }
  }
}

internal fun ClassEdit.toDraft(): CourseEditorDraft =
  CourseEditorDraft(
    name = name,
    note = note,
    startWeek = weekPattern.startWeek.toString(),
    endWeek = weekPattern.endWeek.toString(),
    parity = weekPattern.parity,
    activeWeeks = weekPattern.activeWeeks?.sorted()?.joinToString(",").orEmpty(),
    reminderMode =
      when (reminderOverride?.enabled) {
        null -> MeetingReminderMode.INHERIT
        true -> MeetingReminderMode.ENABLED
        false -> MeetingReminderMode.DISABLED
      },
    reminderMinutes = reminderOverride?.minutesBefore?.toString() ?: "10",
    reminderDelivery = reminderOverride?.delivery ?: ReminderDelivery.POPUP,
  )

private fun String.toWeekSet(): Set<Int>? {
  if (isBlank()) return null
  val weeks = trim().split(Regex("[,，\\s]+")).map(String::toIntOrNull)
  return if (weeks.any { it == null }) null else weeks.filterNotNull().toSet()
}
