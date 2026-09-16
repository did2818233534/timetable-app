package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.model.SplitDisplaySlot
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateSplitDisplaySlotTest {
  private val source = SampleTimetableRepository().timetable.value
  private val update = UpdateSplitDisplaySlot()

  @Test
  fun enableAndDisable_updatesOnlyRequestedSlot() {
    val enabled = update(source, DayOfWeek.TUESDAY, 2, true)
    val disabled = update(enabled, DayOfWeek.TUESDAY, 2, false)

    assertEquals(setOf(SplitDisplaySlot(DayOfWeek.TUESDAY, 2)), enabled.splitDisplaySlots)
    assertEquals(emptySet<SplitDisplaySlot>(), disabled.splitDisplaySlots)
  }
}
