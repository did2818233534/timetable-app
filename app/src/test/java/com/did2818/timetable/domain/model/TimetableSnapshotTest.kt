package com.did2818.timetable.domain.model

import com.did2818.timetable.data.sample.SampleTimetableRepository
import org.junit.Assert.assertThrows
import org.junit.Test

class TimetableSnapshotTest {
  @Test
  fun snapshot_rejectsMeetingWithoutMatchingCourse() {
    val sample = SampleTimetableRepository().timetable.value
    val orphan = sample.meetings.first().copy(courseId = "missing")

    assertThrows(IllegalArgumentException::class.java) {
      TimetableSnapshot(
        term = sample.term,
        periods = sample.periods,
        courses = sample.courses,
        meetings = listOf(orphan),
      )
    }
  }
}
