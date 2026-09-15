package com.did2818.timetable.data.importexport

import kotlinx.serialization.Serializable

@Serializable
internal data class TimetableDocument(
  val formatVersion: Int,
  val term: TermDocument,
  val periods: List<PeriodDocument>,
  val courses: List<CourseDocument>,
)

@Serializable
internal data class TermDocument(
  val name: String,
  val startDate: String,
  val totalWeeks: Int,
)

@Serializable
internal data class PeriodDocument(
  val number: Int,
  val startTime: String,
  val endTime: String,
  val breakAfter: String? = null,
)

@Serializable
internal data class CourseDocument(
  val name: String,
  val teacher: String = "",
  val color: String? = null,
  val note: String = "",
  val meetings: List<MeetingDocument>,
)

@Serializable
internal data class MeetingDocument(
  val day: String,
  val period: Int,
  val classroom: String = "",
  val startWeek: Int,
  val endWeek: Int,
  val parity: String = "EVERY_WEEK",
  val excludedWeeks: Set<Int> = emptySet(),
)
