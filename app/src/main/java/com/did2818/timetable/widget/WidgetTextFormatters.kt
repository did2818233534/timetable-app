package com.did2818.timetable.widget

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val monthDayFormatter = DateTimeFormatter.ofPattern("M/d")
private val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

internal fun LocalDate.widgetDateText(): String = format(monthDayFormatter)

internal fun LocalTime.widgetTimeText(): String = format(timeFormatter)

internal fun DayOfWeek.widgetDayText(): String =
  when (this) {
    DayOfWeek.MONDAY -> "一"
    DayOfWeek.TUESDAY -> "二"
    DayOfWeek.WEDNESDAY -> "三"
    DayOfWeek.THURSDAY -> "四"
    DayOfWeek.FRIDAY -> "五"
    DayOfWeek.SATURDAY -> "六"
    DayOfWeek.SUNDAY -> "日"
  }
