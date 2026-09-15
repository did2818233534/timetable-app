package com.did2818.timetable.data.importexport

import com.did2818.timetable.domain.model.TimetableSnapshot
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class TimetableJsonCodec {
  private val mapper = TimetableDocumentMapper()
  private val json =
    Json {
      prettyPrint = true
      encodeDefaults = false
      explicitNulls = false
    }

  fun decode(document: String): TimetableSnapshot =
    try {
      mapper.toSnapshot(json.decodeFromString<TimetableDocument>(document))
    } catch (error: TimetableImportException) {
      throw error
    } catch (error: SerializationException) {
      throw TimetableImportException("文件不是有效的课表 JSON，或缺少必填字段", error)
    } catch (error: IllegalArgumentException) {
      throw TimetableImportException(error.message ?: "课表内容无效", error)
    }

  fun encode(timetable: TimetableSnapshot): String =
    json.encodeToString(TimetableDocument.serializer(), mapper.fromSnapshot(timetable))
}
