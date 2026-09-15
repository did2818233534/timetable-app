package com.did2818.timetable.data.sample

import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.repository.TimetableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SampleTimetableRepository : TimetableRepository {
  private val mutableTimetable =
    MutableStateFlow(
      TimetableSnapshot(
        term = SampleAcademicData.term,
        periods = SampleAcademicData.periods,
        courses = sampleCourses,
        meetings = sampleMeetings,
      ),
    )

  override val timetable: StateFlow<TimetableSnapshot> = mutableTimetable.asStateFlow()
}
