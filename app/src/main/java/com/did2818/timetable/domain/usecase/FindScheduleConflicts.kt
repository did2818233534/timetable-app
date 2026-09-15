package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.ScheduleConflict
import kotlin.math.max
import kotlin.math.min

/** Finds meetings that overlap on the same day during at least one active week. */
class FindScheduleConflicts {
  operator fun invoke(meetings: List<ClassMeeting>): List<ScheduleConflict> =
    meetings
      .groupBy(ClassMeeting::dayOfWeek)
      .values
      .flatMap(::conflictsWithinDay)

  private fun conflictsWithinDay(meetings: List<ClassMeeting>): List<ScheduleConflict> =
    buildList {
      meetings.forEachIndexed { firstIndex, first ->
        for (secondIndex in firstIndex + 1 until meetings.size) {
          val second = meetings[secondIndex]
          if (!timesOverlap(first, second)) continue

          val conflictingWeeks = activeWeeksSharedBy(first, second)
          if (conflictingWeeks.isNotEmpty()) {
            add(ScheduleConflict(first, second, conflictingWeeks))
          }
        }
      }
    }

  private fun timesOverlap(first: ClassMeeting, second: ClassMeeting): Boolean =
    first.startTime < second.endTime && second.startTime < first.endTime

  private fun activeWeeksSharedBy(
    first: ClassMeeting,
    second: ClassMeeting,
  ): Set<Int> {
    val firstPossibleWeek = max(first.weekPattern.startWeek, second.weekPattern.startWeek)
    val lastPossibleWeek = min(first.weekPattern.endWeek, second.weekPattern.endWeek)
    if (firstPossibleWeek > lastPossibleWeek) return emptySet()

    return (firstPossibleWeek..lastPossibleWeek)
      .filterTo(linkedSetOf()) { week ->
        first.isActiveIn(week) && second.isActiveIn(week)
      }
  }
}
