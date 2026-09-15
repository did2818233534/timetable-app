package com.did2818.timetable.domain.repository

import com.did2818.timetable.domain.model.TimetableSnapshot

/** Imports one complete timetable document and makes it the active timetable. */
fun interface TimetableImporter {
  suspend fun import(document: String): TimetableSnapshot
}
