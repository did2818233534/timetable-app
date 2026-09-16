package com.did2818.timetable.domain.model

import java.time.DayOfWeek

/** A consistent read of all data required to render and validate a timetable. */
data class TimetableSnapshot(
  val term: AcademicTerm,
  val periods: List<ClassPeriod>,
  val courses: List<Course>,
  val meetings: List<ClassMeeting>,
  val visibleDays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
  val hideEmptyDays: Boolean = false,
  val reminderSettings: ReminderSettings = ReminderSettings(),
  val splitDisplaySlots: Set<SplitDisplaySlot> = emptySet(),
) {
  init {
    require(visibleDays.isNotEmpty()) { "At least one weekday must be visible" }
    require(periods.map(ClassPeriod::number).distinct().size == periods.size) {
      "Period numbers must be unique"
    }
    require(periods.map { it.startTime to it.endTime }.distinct().size == periods.size) {
      "Period times must be unique"
    }
    require(courses.map(Course::id).distinct().size == courses.size) {
      "Course ids must be unique"
    }
    val courseIds = courses.mapTo(hashSetOf(), Course::id)
    require(meetings.all { it.courseId in courseIds }) {
      "Every meeting must reference an existing course"
    }
    val periodNumbers = periods.mapTo(hashSetOf(), ClassPeriod::number)
    require(splitDisplaySlots.all { it.periodNumber in periodNumbers }) {
      "Every split display slot must reference an existing period"
    }
  }
}
