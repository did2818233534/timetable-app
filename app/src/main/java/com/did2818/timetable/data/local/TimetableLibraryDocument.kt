package com.did2818.timetable.data.local

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class TimetableLibraryDocument(
  val formatVersion: Int = 1,
  val selectedId: String? = null,
  val entries: List<TimetableLibraryEntry> = emptyList(),
)

@Serializable
internal data class TimetableLibraryEntry(val id: String)

internal class TimetableLibraryCodec {
  private val json =
    Json {
      prettyPrint = true
      ignoreUnknownKeys = true
    }

  fun encode(document: TimetableLibraryDocument): String = json.encodeToString(document)

  fun decode(value: String): TimetableLibraryDocument =
    json.decodeFromString<TimetableLibraryDocument>(value).also {
      require(it.formatVersion == 1) { "Unsupported timetable library version" }
      require(it.entries.map(TimetableLibraryEntry::id).distinct().size == it.entries.size) {
        "Timetable library contains duplicate ids"
      }
    }
}
