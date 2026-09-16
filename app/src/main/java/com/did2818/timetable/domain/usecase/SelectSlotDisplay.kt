package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.SlotDisplay
import com.did2818.timetable.domain.model.SlotDisplayGroup
import com.did2818.timetable.domain.model.WeekScheduleItem

class SelectSlotDisplays {
  operator fun invoke(
    candidates: List<WeekScheduleItem>,
    selectedWeek: Int,
    totalWeeks: Int,
    limit: Int = DEFAULT_DISPLAY_LIMIT,
  ): SlotDisplayGroup {
    require(limit > 0) { "Display limit must be positive" }
    val hasConflict = FindScheduleConflicts()(candidates.map { it.meeting }).isNotEmpty()
    val ranked =
      candidates.sortedWith(
        compareBy<WeekScheduleItem> { it.displayRank(selectedWeek, totalWeeks).category }
          .thenBy { it.displayRank(selectedWeek, totalWeeks).weekDistance }
          .thenBy { it.meeting.id },
      )
    return SlotDisplayGroup(
      items = ranked.take(limit).map { it.copy(isActive = it.meeting.isActiveIn(selectedWeek)) },
      hasConflict = hasConflict,
    )
  }
}

class SelectSlotDisplay(private val selectDisplays: SelectSlotDisplays = SelectSlotDisplays()) {
  operator fun invoke(
    candidates: List<WeekScheduleItem>,
    selectedWeek: Int,
    totalWeeks: Int,
  ): SlotDisplay? {
    val group = selectDisplays(candidates, selectedWeek, totalWeeks, limit = 1)
    return group.items.firstOrNull()?.let { SlotDisplay(it, group.hasConflict) }
  }
}

private fun WeekScheduleItem.displayRank(selectedWeek: Int, totalWeeks: Int): DisplayRank {
  if (meeting.isActiveIn(selectedWeek)) return DisplayRank(ACTIVE, 0)
  val next = firstActiveWeek(selectedWeek + 1..totalWeeks)
  if (next != null) return DisplayRank(FUTURE, next - selectedWeek)
  val previous = lastActiveWeek(1 until selectedWeek)
  if (previous != null) return DisplayRank(PAST, selectedWeek - previous)
  return DisplayRank(NEVER, Int.MAX_VALUE)
}

private fun WeekScheduleItem.firstActiveWeek(weeks: IntRange): Int? =
  weeks.firstOrNull(meeting::isActiveIn)

private fun WeekScheduleItem.lastActiveWeek(weeks: IntRange): Int? =
  weeks.lastOrNull(meeting::isActiveIn)

private data class DisplayRank(val category: Int, val weekDistance: Int)

private const val ACTIVE = 0
private const val FUTURE = 1
private const val PAST = 2
private const val NEVER = 3
private const val DEFAULT_DISPLAY_LIMIT = 2
