package com.did2818.timetable.domain.repository

import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.TimetableSummary
import kotlinx.coroutines.flow.StateFlow

/** Atomic timetable data boundary implemented by the data layer. */
interface TimetableRepository {
  val timetable: StateFlow<TimetableSnapshot>
  val timetables: StateFlow<List<TimetableSummary>>
  val selectedTimetableId: StateFlow<String?>

  suspend fun replace(timetable: TimetableSnapshot)

  suspend fun addAndSelect(timetable: TimetableSnapshot)

  suspend fun select(timetableId: String)
}
