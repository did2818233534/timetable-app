@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.did2818.timetable.data.importexport

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
internal data class TimetableDocument(
  val formatVersion: Int,
  val term: TermDocument,
  val periods: List<PeriodDocument>,
  @EncodeDefault
  val visibleDays: List<String> =
    listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
  @EncodeDefault
  val hideEmptyDays: Boolean = false,
  @EncodeDefault
  val reminderSettings: ReminderSettingsDocument = ReminderSettingsDocument(),
  @EncodeDefault
  val splitDisplaySlots: List<SplitDisplaySlotDocument> = emptyList(),
  val courses: List<CourseDocument>,
)

@Serializable
internal data class SplitDisplaySlotDocument(
  val day: String,
  val period: Int,
)

@Serializable
internal data class ReminderSettingsDocument(
  @EncodeDefault
  val scope: String = "DISABLED",
  @EncodeDefault
  val minutesBefore: Int = 10,
  @EncodeDefault
  val delivery: String = "POPUP",
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
  @EncodeDefault
  val visible: Boolean = true,
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
  val note: String = "",
  val startWeek: Int,
  val endWeek: Int,
  val parity: String = "EVERY_WEEK",
  val excludedWeeks: Set<Int> = emptySet(),
  val activeWeeks: Set<Int>? = null,
  val reminder: ReminderOverrideDocument? = null,
)

@Serializable
internal data class ReminderOverrideDocument(
  val enabled: Boolean,
  @EncodeDefault
  val minutesBefore: Int = 10,
  @EncodeDefault
  val delivery: String = "POPUP",
)
