package com.did2818.timetable.data.local

import com.did2818.timetable.data.importexport.TimetableJsonCodec
import com.did2818.timetable.data.sample.SampleTimetableRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FileTimetableRepositoryTest {
  @get:Rule val temporaryFolder = TemporaryFolder()

  @Test
  fun replacement_survivesRepositoryRecreation() = runTest {
    val codec = TimetableJsonCodec()
    val fallback = SampleTimetableRepository().timetable.value
    val imported = codec.decode(resourceText("timetable-valid.json"))
    val directory = temporaryFolder.newFolder("repository")
    val repository = FileTimetableRepository(directory, fallback, codec)

    repository.replace(imported)
    repository.replace(imported)

    val recreated = FileTimetableRepository(directory, fallback, codec)
    assertEquals(imported, recreated.timetable.value)
  }

  private fun resourceText(name: String): String =
    checkNotNull(javaClass.classLoader?.getResource(name)).readText()
}
