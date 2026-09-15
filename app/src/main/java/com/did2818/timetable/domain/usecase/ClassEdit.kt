package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.ReminderOverride

data class ClassEdit(
  val name: String,
  val note: String,
  val weekPattern: WeekPattern,
  val reminderOverride: ReminderOverride? = null,
)
