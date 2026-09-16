package com.did2818.timetable.data.export.spreadsheet

import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderOverride
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import java.time.DayOfWeek

internal fun DayOfWeek.chineseName(): String =
  listOf("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")[value - 1]

internal fun WeekPattern.ruleName(): String =
  when (parity) {
    WeekParity.EVERY_WEEK -> "每周"
    WeekParity.ODD_WEEKS -> "单周"
    WeekParity.EVEN_WEEKS -> "双周"
  }

internal fun WeekPattern.summary(): String {
  activeWeeks?.let { return "第${it.sorted().joinToString("、")}周" }
  val base = "$startWeek–$endWeek 周 ${ruleName()}"
  return if (excludedWeeks.isEmpty()) base
  else "$base（不含第${excludedWeeks.sorted().joinToString("、")}周）"
}

internal fun ReminderOverride?.summary(): String =
  when {
    this == null -> "遵循全局"
    !enabled -> "不提醒"
    else -> "$minutesBefore 分钟前${delivery.chineseName()}"
  }

private fun ReminderDelivery.chineseName(): String =
  when (this) {
    ReminderDelivery.POPUP -> "弹窗"
    ReminderDelivery.ALARM -> "闹钟"
  }
