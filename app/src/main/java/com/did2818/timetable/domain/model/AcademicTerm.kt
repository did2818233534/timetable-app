package com.did2818.timetable.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** A semester whose first week starts on [startDate]. */
data class AcademicTerm(
  val id: String,
  val name: String,
  val startDate: LocalDate,
  val totalWeeks: Int,
) {
  init {
    require(id.isNotBlank()) { "Term id cannot be blank" }
    require(name.isNotBlank()) { "Term name cannot be blank" }
    require(startDate.dayOfWeek == DayOfWeek.MONDAY) {
      "The first day of a term must be a Monday"
    }
    require(totalWeeks in 1..30) { "Total weeks must be between 1 and 30" }
  }

  val endDate: LocalDate
    get() = startDate.plusDays(totalWeeks * DAYS_PER_WEEK - 1L)

  /** Returns a one-based semester week, or null when [date] is outside this term. */
  fun weekNumberOn(date: LocalDate): Int? {
    if (date < startDate || date > endDate) return null
    return (ChronoUnit.DAYS.between(startDate, date) / DAYS_PER_WEEK).toInt() + 1
  }

  fun dateOf(week: Int, dayOfWeek: DayOfWeek): LocalDate {
    require(week in 1..totalWeeks) { "Week must be inside the term" }
    return startDate
      .plusWeeks((week - 1).toLong())
      .plusDays((dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
  }

  private companion object {
    const val DAYS_PER_WEEK = 7L
  }
}
