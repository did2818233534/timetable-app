package com.did2818.timetable.domain.model

import java.time.LocalTime
import org.junit.Assert.assertThrows
import org.junit.Test

class ClassPeriodTest {
  @Test
  fun period_rejectsInvalidNumberAndTimeRange() {
    assertThrows(IllegalArgumentException::class.java) {
      ClassPeriod(0, LocalTime.of(8, 0), LocalTime.of(9, 0))
    }
    assertThrows(IllegalArgumentException::class.java) {
      ClassPeriod(1, LocalTime.of(9, 0), LocalTime.of(8, 0))
    }
  }
}
