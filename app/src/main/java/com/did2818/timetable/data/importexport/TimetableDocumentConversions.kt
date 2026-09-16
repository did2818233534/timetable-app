package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderOverride
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.model.ReminderSettings
import com.did2818.timetable.domain.model.SplitDisplaySlot
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

internal fun TermDocument.toModel(): AcademicTerm {
  require(name.isNotBlank()) { "学期名称不能为空" }
  require(totalWeeks in 1..30) { "学期总周数必须在 1 到 30 之间" }
  val date = parseValue("开学日期", startDate, LocalDate::parse)
  require(date.dayOfWeek == DayOfWeek.MONDAY) { "开学日期必须是第一周的周一" }
  return AcademicTerm("imported-$date", name.trim(), date, totalWeeks)
}

internal fun PeriodDocument.toModel(): ClassPeriod {
  require(number >= 1) { "节次编号必须是正整数" }
  val start = parseValue("上课时间", startTime, LocalTime::parse)
  val end = parseValue("下课时间", endTime, LocalTime::parse)
  require(start < end) { "下课时间必须晚于上课时间" }
  return ClassPeriod(number, start, end, breakAfter?.trim(), visible)
}

internal fun List<String>.toVisibleDays(): Set<DayOfWeek> {
  require(isNotEmpty()) { "至少需要显示一天" }
  val days = map { parseValue("显示星期", it.uppercase(), DayOfWeek::valueOf) }
  require(days.distinct().size == days.size) { "显示星期不能重复" }
  return days.toSet()
}

internal fun CourseDocument.toModel(index: Int): Course {
  require(name.isNotBlank()) { "课程名称不能为空" }
  return Course(
    id = "course-${index + 1}",
    name = name.trim(),
    teacher = teacher.trim(),
    colorArgb = color?.toArgb() ?: defaultColor(index),
    note = note.trim(),
  )
}

internal fun ReminderSettingsDocument.toModel(): ReminderSettings =
  ReminderSettings(
    scope = parseValue("全局提醒范围", scope.uppercase(), ReminderScope::valueOf),
    minutesBefore = minutesBefore,
    delivery = parseValue("全局提醒方式", delivery.uppercase(), ReminderDelivery::valueOf),
  )

internal fun ReminderOverrideDocument.toModel(): ReminderOverride =
  ReminderOverride(
    enabled = enabled,
    minutesBefore = minutesBefore,
    delivery = parseValue("课程提醒方式", delivery.uppercase(), ReminderDelivery::valueOf),
  )

internal fun SplitDisplaySlotDocument.toModel(periods: List<ClassPeriod>): SplitDisplaySlot {
  val dayOfWeek = parseValue("分栏显示星期", day.uppercase(), DayOfWeek::valueOf)
  require(periods.any { it.number == period }) { "分栏显示引用了不存在的第 $period 节" }
  return SplitDisplaySlot(dayOfWeek, period)
}

internal fun MeetingDocument.toModel(
  courseId: String,
  index: Int,
  periods: List<ClassPeriod>,
  totalWeeks: Int,
): ClassMeeting {
  require(startWeek >= 1 && endWeek >= startWeek) { "课程起止周数无效" }
  require(endWeek <= totalWeeks) { "课程周数不能超过学期总周数" }
  require(excludedWeeks.all { it in startWeek..endWeek }) { "排除周必须在课程起止周内" }
  require(activeWeeks?.all { it in startWeek..endWeek } != false) { "指定周次必须在课程起止周内" }
  val classPeriod = periods.singleOrNull { it.number == period } ?: error("引用了不存在的第 $period 节")
  return ClassMeeting(
    id = "$courseId-meeting-${index + 1}",
    courseId = courseId,
    dayOfWeek = parseValue("星期", day.uppercase(), DayOfWeek::valueOf),
    startTime = classPeriod.startTime,
    endTime = classPeriod.endTime,
    note = note.ifBlank { classroom }.trim(),
    weekPattern =
      WeekPattern(
        startWeek = startWeek,
        endWeek = endWeek,
        parity = parseValue("单双周", parity.uppercase(), WeekParity::valueOf),
        excludedWeeks = excludedWeeks,
        activeWeeks = activeWeeks,
      ),
    reminderOverride = reminder?.toModel(),
  )
}

private fun String.toArgb(): Long {
  val digits = removePrefix("#")
  require(digits.length == 6 || digits.length == 8) { "颜色必须是 #RRGGBB 或 #AARRGGBB" }
  return (if (digits.length == 6) "FF$digits" else digits).toLong(16)
}

private fun defaultColor(index: Int): Long = DEFAULT_COLORS[index % DEFAULT_COLORS.size]

private fun <T> parseValue(label: String, value: String, parser: (String) -> T): T =
  runCatching { parser(value) }.getOrElse { throw IllegalArgumentException("$label 格式错误：$value", it) }

private val DEFAULT_COLORS = listOf(0xFF4F6BEDL, 0xFF00897BL, 0xFF8E5CC7L, 0xFFE06C45L)
