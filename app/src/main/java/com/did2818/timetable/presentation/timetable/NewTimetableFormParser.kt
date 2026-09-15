package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.usecase.NewTimetableSpec
import com.did2818.timetable.domain.usecase.PeriodTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal class NewTimetableFormParser {
  operator fun invoke(
    name: String,
    startDateText: String,
    totalWeeksText: String,
    periodsText: String,
  ): NewTimetableSpec {
    require(name.isNotBlank()) { "课表名称不能为空" }
    val startDate = parseDate(startDateText)
    require(startDate.dayOfWeek == DayOfWeek.MONDAY) { "第一周日期必须是周一" }
    val totalWeeks = totalWeeksText.toIntOrNull() ?: error("总周数必须是整数")
    require(totalWeeks in 1..30) { "总周数必须在 1 到 30 之间" }
    val periods = periodsText.lineSequence().map(String::trim).filter(String::isNotEmpty).map(::parsePeriod).toList()
    require(periods.isNotEmpty()) { "至少需要填写一个节次" }
    return NewTimetableSpec(name.trim(), startDate, totalWeeks, periods)
  }

  private fun parseDate(value: String): LocalDate =
    runCatching { LocalDate.parse(value.trim()) }.getOrElse { error("日期格式应为 YYYY-MM-DD") }

  private fun parsePeriod(value: String): PeriodTime {
    val match = PERIOD_PATTERN.matchEntire(value) ?: error("节次格式错误：$value")
    val start = parseTime(match.groupValues[1], value)
    val end = parseTime(match.groupValues[2], value)
    require(start < end) { "下课时间必须晚于上课时间：$value" }
    return PeriodTime(start, end)
  }

  private fun parseTime(value: String, line: String): LocalTime =
    runCatching { LocalTime.parse(value, TIME_FORMAT) }.getOrElse { error("节次格式错误：$line") }
}

private val PERIOD_PATTERN = Regex("""^(\d{1,2}:\d{2})\s*[-–—]\s*(\d{1,2}:\d{2})$""")
private val TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm")
