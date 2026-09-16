package com.did2818.timetable.data.local

import com.did2818.timetable.data.importexport.TimetableJsonCodec
import com.did2818.timetable.data.sample.SampleTimetableRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    assertEquals(1, recreated.timetables.value.size)
  }

  @Test
  fun addAndSelect_retainsPreviousTimetableAndPersistsSelection() = runTest {
    val codec = TimetableJsonCodec()
    val first = codec.decode(resourceText("timetable-valid.json"))
    val second =
      first.copy(term = first.term.copy(id = "second", name = "第二份课表"))
    val directory = temporaryFolder.newFolder("multi-repository")
    val repository = FileTimetableRepository(directory, first, codec)

    repository.replace(first)
    repository.addAndSelect(second)

    assertEquals(2, repository.timetables.value.size)
    assertEquals("第二份课表", repository.timetable.value.term.name)
    val firstId = repository.timetables.value.first().id
    repository.select(firstId)

    val recreated = FileTimetableRepository(directory, second, codec)
    assertEquals(2, recreated.timetables.value.size)
    assertEquals(first, recreated.timetable.value)
  }

  @Test
  fun legacyActiveTimetable_isMigratedIntoLibrary() {
    val codec = TimetableJsonCodec()
    val timetable = codec.decode(resourceText("timetable-valid.json"))
    val directory = temporaryFolder.newFolder("legacy-repository")
    java.io.File(directory, "active-timetable.json").writeText(codec.encode(timetable))

    val repository = FileTimetableRepository(directory, timetable, codec)

    assertEquals(timetable, repository.timetable.value)
    assertEquals(1, repository.timetables.value.size)
    assertTrue(java.io.File(directory, "timetables/index.json").isFile)
  }

  private fun resourceText(name: String): String =
    checkNotNull(javaClass.classLoader?.getResource(name)).readText()
}
