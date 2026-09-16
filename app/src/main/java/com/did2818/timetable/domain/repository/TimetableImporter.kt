package com.did2818.timetable.domain.repository

import com.did2818.timetable.domain.model.TimetableSnapshot

/** Imports one complete timetable document, retains existing timetables, and selects the new one. */
fun interface TimetableImporter {
  suspend fun import(document: String): TimetableSnapshot
}
