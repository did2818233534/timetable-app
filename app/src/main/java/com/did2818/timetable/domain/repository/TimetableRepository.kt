package com.did2818.timetable.domain.repository

import com.did2818.timetable.domain.model.AcademicTerm
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import kotlinx.coroutines.flow.Flow

/** Storage boundary; Room-backed implementation will live in the data layer. */
interface TimetableRepository {
  fun observeCurrentTerm(): Flow<AcademicTerm?>

  fun observeCourses(): Flow<List<Course>>

  fun observeMeetings(): Flow<List<ClassMeeting>>
}
