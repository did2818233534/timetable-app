package com.did2818.timetable.domain.model

import java.time.LocalDate

/** One calendar day aligned to the configured period rows. */
data class DaySchedule(
  val date: LocalDate,
  val periodItems: List<List<WeekScheduleItem>>,
)
