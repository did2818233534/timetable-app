package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.SlotDisplay
import com.did2818.timetable.domain.model.WeekScheduleItem

class SelectSlotDisplay {
  operator fun invoke(
    candidates: List<WeekScheduleItem>,
    selectedWeek: Int,
    totalWeeks: Int,
  ): SlotDisplay? {
    if (candidates.isEmpty()) return null
    val hasConflict = FindScheduleConflicts()(candidates.map { it.meeting }).isNotEmpty()
    val active = candidates.filter { it.meeting.isActiveIn(selectedWeek) }.sortedBy { it.meeting.id }
    if (active.isNotEmpty()) return SlotDisplay(active.first().copy(isActive = true), hasConflict)

    val next =
      candidates
        .mapNotNull { item -> item.firstActiveWeek(selectedWeek + 1..totalWeeks)?.let { it to item } }
        .minWithOrNull(compareBy<Pair<Int, WeekScheduleItem>>({ it.first }, { it.second.meeting.id }))
    if (next != null) return SlotDisplay(next.second.copy(isActive = false), hasConflict)

    val previous =
      candidates
        .mapNotNull { item -> item.lastActiveWeek(1 until selectedWeek)?.let { it to item } }
        .maxWithOrNull(compareBy<Pair<Int, WeekScheduleItem>>({ it.first }, { it.second.meeting.id }))
    return previous?.let { SlotDisplay(it.second.copy(isActive = false), hasConflict) }
      ?: SlotDisplay(candidates.first().copy(isActive = false), hasConflict)
  }
}

private fun WeekScheduleItem.firstActiveWeek(weeks: IntRange): Int? =
  weeks.firstOrNull(meeting::isActiveIn)

private fun WeekScheduleItem.lastActiveWeek(weeks: IntRange): Int? =
  weeks.lastOrNull(meeting::isActiveIn)
