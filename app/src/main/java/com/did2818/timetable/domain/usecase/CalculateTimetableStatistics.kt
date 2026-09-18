package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.TimetableStatistics
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

class CalculateTimetableStatistics {
  operator fun invoke(
    timetable: TimetableSnapshot,
    now: LocalDateTime,
  ): TimetableStatistics {
    val occurrences = timetable.occurrences()
    val remaining = occurrences.filter { it.endsAt.isAfter(now) }
    val currentMonday = now.toLocalDate().monday()
    val currentSunday = currentMonday.plusDays(6)
    val remainingThisWeek = remaining.filter { it.date in currentMonday..currentSunday }
    val lastClassAt = remaining.maxOfOrNull(Occurrence::endsAt)

    return TimetableStatistics(
      remainingThisWeek = remainingThisWeek.size,
      weeksUntilLastClass = lastClassAt?.weeksFrom(currentMonday),
      remainingClasses = remaining.size,
      completedClasses = occurrences.size - remaining.size,
      remainingByDay =
        DayOfWeek.entries.associateWith { day ->
          remainingThisWeek.count { it.date.dayOfWeek == day }
        },
      lastClassAt = lastClassAt,
    )
  }
}

private data class Occurrence(val date: LocalDate, val endsAt: LocalDateTime)

private fun TimetableSnapshot.occurrences(): List<Occurrence> =
  meetings.flatMap { meeting ->
    (1..term.totalWeeks).mapNotNull { week -> meeting.occurrenceIn(this, week) }
  }

private fun ClassMeeting.occurrenceIn(
  timetable: TimetableSnapshot,
  week: Int,
): Occurrence? {
  if (!isActiveIn(week)) return null
  val date = timetable.term.dateOf(week, dayOfWeek)
  return Occurrence(date, date.atTime(endTime))
}

private fun LocalDate.monday(): LocalDate =
  with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

private fun LocalDateTime.weeksFrom(currentMonday: LocalDate): Int =
  ChronoUnit.WEEKS.between(currentMonday, toLocalDate().monday()).toInt().coerceAtLeast(0)
