package com.did2818.timetable.presentation.common.file

import java.io.ByteArrayInputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LimitedTextReaderTest {
  @Test
  fun reader_decodesUtf8() {
    val text = "课程表"

    assertEquals(text, ByteArrayInputStream(text.toByteArray()).readUtf8Text())
  }

  @Test
  fun reader_rejectsOversizedDocument() {
    val stream = ByteArrayInputStream(ByteArray(11))

    assertThrows(IllegalArgumentException::class.java) { stream.readUtf8Text(maxBytes = 10) }
  }
}
