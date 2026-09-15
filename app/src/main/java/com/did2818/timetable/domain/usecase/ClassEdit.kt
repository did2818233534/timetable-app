package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.WeekPattern

data class ClassEdit(
  val name: String,
  val classroom: String,
  val note: String,
  val weekPattern: WeekPattern,
)
