package com.did2818.timetable.data.sample

import com.did2818.timetable.domain.model.Course

internal val sampleCourses =
  listOf(
    Course(id = "math", name = "高等数学", teacher = "张老师", colorArgb = 0xFF4F6BEDL),
    Course(id = "physics", name = "大学物理", teacher = "李老师", colorArgb = 0xFF00897BL),
    Course(id = "english", name = "大学英语", teacher = "王老师", colorArgb = 0xFF8E5CC7L),
    Course(id = "programming", name = "程序设计", teacher = "陈老师", colorArgb = 0xFFE06C45L),
  )
