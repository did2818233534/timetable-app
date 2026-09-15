package com.did2818.timetable.domain.repository

import com.did2818.timetable.domain.model.TimetableSnapshot

fun interface TimetableExporter {
  fun export(timetable: TimetableSnapshot): String
}
