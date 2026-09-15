package com.did2818.timetable.domain.repository

import com.did2818.timetable.domain.model.TimetableSnapshot
import kotlinx.coroutines.flow.StateFlow

/** Atomic timetable data boundary implemented by the data layer. */
interface TimetableRepository {
  val timetable: StateFlow<TimetableSnapshot>
}
