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
    assertEquals("08:00", timetable.periods.single().startTime.toString())
    assertEquals("09:35", timetable.periods.single().endTime.toString())
    assertEquals("午休", timetable.periods.single().breakAfter)
    assertEquals("A101", meeting.classroom)
    assertEquals(WeekParity.ODD_WEEKS, meeting.weekPattern.parity)
    assertEquals(setOf(5), meeting.weekPattern.excludedWeeks)
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
