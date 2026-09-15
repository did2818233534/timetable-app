package com.did2818.timetable.presentation.common.format

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val shortDateFormatter = DateTimeFormatter.ofPattern("M月d日")
private val monthDayFormatter = DateTimeFormatter.ofPattern("M/d")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun LocalDate.shortDateText(): String = format(shortDateFormatter)

fun LocalDate.monthDayText(): String = format(monthDayFormatter)

fun LocalTime.timeText(): String = format(timeFormatter)

fun DayOfWeek.chineseText(): String =
  when (this) {
    DayOfWeek.MONDAY -> "周一"
    DayOfWeek.TUESDAY -> "周二"
    DayOfWeek.WEDNESDAY -> "周三"
    DayOfWeek.THURSDAY -> "周四"
    DayOfWeek.FRIDAY -> "周五"
    DayOfWeek.SATURDAY -> "周六"
    DayOfWeek.SUNDAY -> "周日"
  }
