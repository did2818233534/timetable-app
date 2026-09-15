package com.did2818.timetable.data.sample

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/** Temporary local data used until the Room-backed repository is introduced. */
object SampleTimetableData {
  val term =
    AcademicTerm(
      id = "2026-autumn",
      name = "2026 秋季学期",
      startDate = LocalDate.of(2026, 9, 7),
      totalWeeks = 18,
    )

  val courses =
    listOf(
      Course(id = "math", name = "高等数学", teacher = "张老师", colorArgb = 0xFF4F6BEDL),
      Course(id = "physics", name = "大学物理", teacher = "李老师", colorArgb = 0xFF00897BL),
      Course(id = "english", name = "大学英语", teacher = "王老师", colorArgb = 0xFF8E5CC7L),
      Course(id = "programming", name = "程序设计", teacher = "陈老师", colorArgb = 0xFFE06C45L),
    )

  val periods =
    listOf(
      ClassPeriod(1, LocalTime.of(8, 0), LocalTime.of(9, 35)),
      ClassPeriod(2, LocalTime.of(10, 0), LocalTime.of(11, 35)),
      ClassPeriod(3, LocalTime.of(14, 0), LocalTime.of(15, 35)),
      ClassPeriod(4, LocalTime.of(15, 50), LocalTime.of(17, 25)),
      ClassPeriod(5, LocalTime.of(19, 0), LocalTime.of(20, 35)),
    )

  val meetings =
    listOf(
      meeting("math-1", "math", DayOfWeek.MONDAY, 8, 0, 9, 35, "博学楼 A101"),
      meeting(
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
      meeting("english-1", "english", DayOfWeek.WEDNESDAY, 14, 0, 15, 35, "明德楼 B305"),
      meeting(
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

  private fun meeting(
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
}
