package com.did2818.timetable.data.sample

import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.TimetableSummary
import com.did2818.timetable.domain.repository.TimetableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SampleTimetableRepository : TimetableRepository {
  private var nextId = 2
  private val snapshots = linkedMapOf("sample-1" to sampleTimetable())
  private val mutableTimetable =
    MutableStateFlow(snapshots.getValue("sample-1"))
  private val mutableTimetables = MutableStateFlow(snapshots.toSummaries())
  private val mutableSelectedId = MutableStateFlow<String?>("sample-1")

  override val timetable: StateFlow<TimetableSnapshot> = mutableTimetable.asStateFlow()
  override val timetables: StateFlow<List<TimetableSummary>> = mutableTimetables.asStateFlow()
  override val selectedTimetableId: StateFlow<String?> = mutableSelectedId.asStateFlow()

  override suspend fun replace(timetable: TimetableSnapshot) {
    val id = checkNotNull(mutableSelectedId.value)
    snapshots[id] = timetable
    mutableTimetable.value = timetable
    mutableTimetables.value = snapshots.toSummaries()
  }

  override suspend fun addAndSelect(timetable: TimetableSnapshot) {
    val id = "sample-${nextId++}"
    snapshots[id] = timetable
    mutableSelectedId.value = id
    mutableTimetable.value = timetable
    mutableTimetables.value = snapshots.toSummaries()
  }

  override suspend fun select(timetableId: String) {
    mutableTimetable.value = snapshots.getValue(timetableId)
    mutableSelectedId.value = timetableId
  }

  private fun Map<String, TimetableSnapshot>.toSummaries() =
    map { (id, snapshot) -> TimetableSummary(id, snapshot.term.name) }
}

private fun sampleTimetable() =
  TimetableSnapshot(
    term = SampleAcademicData.term,
    periods = SampleAcademicData.periods,
    courses = sampleCourses,
    meetings = sampleMeetings,
  )
