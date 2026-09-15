package com.did2818.timetable.data.sample

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import java.time.DayOfWeek
import java.time.LocalTime

internal val sampleMeetings =
  listOf(
    sampleMeeting("math-1", "math", DayOfWeek.MONDAY, 8, 0, 9, 35, "博学楼 A101"),
    sampleMeeting(
      "physics-1",
      "physics",
      DayOfWeek.TUESDAY,
      10,
      0,
      11,
      35,
      "实验楼 203",
      WeekParity.ODD_WEEKS,
    ),
    sampleMeeting("english-1", "english", DayOfWeek.WEDNESDAY, 14, 0, 15, 35, "明德楼 B305"),
    sampleMeeting(
      "programming-1",
      "programming",
      DayOfWeek.FRIDAY,
      15,
      50,
      17,
      25,
      "计算中心 4",
      WeekParity.EVEN_WEEKS,
    ),
  )

private fun sampleMeeting(
  id: String,
  courseId: String,
  day: DayOfWeek,
  startHour: Int,
  startMinute: Int,
  endHour: Int,
  endMinute: Int,
  classroom: String,
  parity: WeekParity = WeekParity.EVERY_WEEK,
) =
  ClassMeeting(
    id = id,
    courseId = courseId,
    dayOfWeek = day,
    startTime = LocalTime.of(startHour, startMinute),
    endTime = LocalTime.of(endHour, endMinute),
    classroom = classroom,
    weekPattern = WeekPattern(startWeek = 1, endWeek = 18, parity = parity),
  )
