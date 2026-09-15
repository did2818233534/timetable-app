package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.repository.TimetableExporter

class JsonTimetableExporter(
  private val codec: TimetableJsonCodec,
) : TimetableExporter {
  override fun export(timetable: TimetableSnapshot): String = codec.encode(timetable)
}
