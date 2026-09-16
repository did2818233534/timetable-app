package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JsonTimetableImporter(
  private val repository: TimetableRepository,
  private val codec: TimetableJsonCodec,
) : TimetableImporter {
  override suspend fun import(document: String): TimetableSnapshot {
    val timetable = withContext(Dispatchers.Default) { codec.decode(document) }
    repository.addAndSelect(timetable)
    return timetable
  }
}
