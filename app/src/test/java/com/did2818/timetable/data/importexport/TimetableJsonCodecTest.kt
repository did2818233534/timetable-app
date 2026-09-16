package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderOverride
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.model.ReminderSettings
import com.did2818.timetable.domain.model.SplitDisplaySlot
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TimetableJsonCodecTest {
  private val codec = TimetableJsonCodec()
  private val validDocument = resourceText("timetable-valid.json")

  @Test
  fun decode_mapsTermPeriodsAndWeekRules() {
    val timetable = codec.decode(validDocument)
    val meeting = timetable.meetings.single()

    assertEquals("测试学期", timetable.term.name)
    assertEquals(2, timetable.periods.size)
    assertEquals("08:00", timetable.periods.first().startTime.toString())
    assertEquals("09:35", timetable.periods.first().endTime.toString())
    assertEquals(false, timetable.periods.last().visible)
    assertEquals(
      setOf(
        java.time.DayOfWeek.MONDAY,
        java.time.DayOfWeek.TUESDAY,
        java.time.DayOfWeek.SATURDAY,
      ),
      timetable.visibleDays,
    )
    assertEquals("A101", meeting.displayNote)
    assertEquals(WeekParity.ODD_WEEKS, meeting.weekPattern.parity)
    assertEquals(setOf(5), meeting.weekPattern.excludedWeeks)
    assertEquals(setOf(1, 7), meeting.weekPattern.activeWeeks)
    assertEquals(false, timetable.hideEmptyDays)
    assertEquals(ReminderScope.DISABLED, timetable.reminderSettings.scope)
  }

  @Test
  fun encodeAndDecode_preservesTimetable() {
    val timetable = codec.decode(validDocument)

    assertEquals(timetable, codec.decode(codec.encode(timetable)))
  }

  @Test
  fun encodeAndDecode_preservesNewTimetableWithoutCourses() {
    val timetable = codec.decode(validDocument).copy(courses = emptyList(), meetings = emptyList())

    assertEquals(timetable, codec.decode(codec.encode(timetable)))
  }

  @Test
  fun encode_writesEditableLayoutSettingsExplicitly() {
    val document = codec.encode(codec.decode(validDocument).copy(hideEmptyDays = true))

    assertEquals(true, document.contains("\"visibleDays\""))
    assertEquals(true, document.contains("\"hideEmptyDays\": true"))
    assertEquals(true, document.contains("\"visible\": true"))
  }

  @Test
  fun encodeAndDecode_preservesGlobalAndMeetingReminderSettings() {
    val source = codec.decode(validDocument)
    val reminder = ReminderOverride(true, 20, ReminderDelivery.ALARM)
    val timetable =
      source.copy(
        reminderSettings = ReminderSettings(ReminderScope.FIRST_CLASS_OF_DAY, 15),
        meetings = listOf(source.meetings.single().copy(reminderOverride = reminder)),
      )

    val document = codec.encode(timetable)

    assertEquals(timetable, codec.decode(document))
    assertEquals(true, document.contains("\"scope\": \"FIRST_CLASS_OF_DAY\""))
    assertEquals(true, document.contains("\"reminder\""))
  }

  @Test
  fun encodeAndDecode_preservesPerSlotSplitDisplayConfiguration() {
    val timetable =
      codec.decode(validDocument).copy(
        splitDisplaySlots = setOf(SplitDisplaySlot(DayOfWeek.MONDAY, 1)),
      )

    val document = codec.encode(timetable)

    assertEquals(timetable, codec.decode(document))
    assertEquals(true, document.contains("\"splitDisplaySlots\""))
    assertEquals(true, document.contains("\"day\": \"MONDAY\""))
    assertEquals(true, document.contains("\"period\": 1"))
  }

  @Test
  fun decode_withoutSplitDisplayConfiguration_defaultsToSingleCourse() {
    val timetable = codec.decode(validDocument)

    assertEquals(emptySet<SplitDisplaySlot>(), timetable.splitDisplaySlots)
    assertEquals(true, codec.encode(timetable).contains("\"splitDisplaySlots\": []"))
  }

  @Test
  fun decode_rejectsCourseWeekOutsideTerm() {
    val invalidDocument = validDocument.replace("\"endWeek\": 18", "\"endWeek\": 19")

    assertThrows(TimetableImportException::class.java) { codec.decode(invalidDocument) }
  }

  private fun resourceText(name: String): String =
    checkNotNull(javaClass.classLoader?.getResource(name)).readText()
}
