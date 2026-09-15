package com.did2818.timetable.data.local

import com.did2818.timetable.data.importexport.TimetableJsonCodec
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.repository.TimetableRepository
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class FileTimetableRepository(
  directory: File,
  private val fallback: TimetableSnapshot,
  private val codec: TimetableJsonCodec,
) : TimetableRepository {
  private val timetableFile = File(directory, FILE_NAME)
  private val temporaryFile = File(directory, "$FILE_NAME.tmp")
  private val mutableTimetable = MutableStateFlow(loadOrFallback())

  override val timetable: StateFlow<TimetableSnapshot> = mutableTimetable.asStateFlow()

  override suspend fun replace(timetable: TimetableSnapshot) =
    withContext(Dispatchers.IO) {
      temporaryFile.writeText(codec.encode(timetable), Charsets.UTF_8)
      check(temporaryFile.renameTo(timetableFile)) { "无法保存导入的课表" }
      mutableTimetable.value = timetable
    }

  private fun loadOrFallback(): TimetableSnapshot {
    if (!timetableFile.isFile) return fallback
    return runCatching { codec.decode(timetableFile.readText(Charsets.UTF_8)) }.getOrDefault(fallback)
  }
}

private const val FILE_NAME = "active-timetable.json"
