package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.WeekParity
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
  }

  @Test
  fun encodeAndDecode_preservesTimetable() {
    val timetable = codec.decode(validDocument)

    assertEquals(timetable, codec.decode(codec.encode(timetable)))
  }

  @Test
  fun decode_rejectsCourseWeekOutsideTerm() {
    val invalidDocument = validDocument.replace("\"endWeek\": 18", "\"endWeek\": 19")

    assertThrows(TimetableImportException::class.java) { codec.decode(invalidDocument) }
  }

  private fun resourceText(name: String): String =
    checkNotNull(javaClass.classLoader?.getResource(name)).readText()
}
