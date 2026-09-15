package com.did2818.timetable.presentation.timetable

import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek

internal sealed interface CourseEditorTarget {
  val day: DayOfWeek
  val period: ClassPeriod

  data class Existing(
    val item: WeekScheduleItem,
    override val period: ClassPeriod,
  ) : CourseEditorTarget {
    override val day: DayOfWeek = item.meeting.dayOfWeek
  }

  data class New(
    override val day: DayOfWeek,
    override val period: ClassPeriod,
  ) : CourseEditorTarget
}
