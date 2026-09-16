package com.did2818.timetable.data.local

import com.did2818.timetable.data.importexport.TimetableJsonCodec
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.TimetableSummary
import com.did2818.timetable.domain.repository.TimetableRepository
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class FileTimetableRepository(
  directory: File,
  private val fallback: TimetableSnapshot,
  private val codec: TimetableJsonCodec,
) : TimetableRepository {
  private val legacyFile = File(directory, LEGACY_FILE_NAME)
  private val libraryDirectory = File(directory, LIBRARY_DIRECTORY_NAME)
  private val indexFile = File(libraryDirectory, INDEX_FILE_NAME)
  private val libraryCodec = TimetableLibraryCodec()
  private val mutex = Mutex()
  private var storedTimetables = loadLibrary()
  private var selectedId = loadSelectedId(storedTimetables)
  private val mutableTimetable = MutableStateFlow(selectedSnapshot() ?: fallback)
  private val mutableTimetables = MutableStateFlow(storedTimetables.toSummaries())
  private val mutableSelectedTimetableId = MutableStateFlow(selectedId)

  override val timetable: StateFlow<TimetableSnapshot> = mutableTimetable.asStateFlow()
  override val timetables: StateFlow<List<TimetableSummary>> = mutableTimetables.asStateFlow()
  override val selectedTimetableId: StateFlow<String?> = mutableSelectedTimetableId.asStateFlow()

  override suspend fun replace(timetable: TimetableSnapshot) =
    withContext(Dispatchers.IO) {
      mutex.withLock {
        val currentId = selectedId
        if (currentId == null) addAndSelectLocked(timetable)
        else {
          writeTimetable(currentId, timetable)
          writeAtomically(legacyFile, codec.encode(timetable))
          storedTimetables = LinkedHashMap(storedTimetables).apply { put(currentId, timetable) }
          publish(currentId)
        }
      }
    }

  override suspend fun addAndSelect(timetable: TimetableSnapshot) =
    withContext(Dispatchers.IO) { mutex.withLock { addAndSelectLocked(timetable) } }

  override suspend fun select(timetableId: String) =
    withContext(Dispatchers.IO) {
      mutex.withLock {
        require(timetableId in storedTimetables) { "找不到所选课程表" }
        writeIndex(timetableId, storedTimetables.keys)
        writeAtomically(legacyFile, codec.encode(storedTimetables.getValue(timetableId)))
        publish(timetableId)
      }
    }

  private fun addAndSelectLocked(timetable: TimetableSnapshot) {
    val id = UUID.randomUUID().toString()
    val updated = LinkedHashMap(storedTimetables).apply { put(id, timetable) }
    writeTimetable(id, timetable)
    writeIndex(id, updated.keys)
    writeAtomically(legacyFile, codec.encode(timetable))
    storedTimetables = updated
    publish(id)
  }

  private fun publish(activeId: String) {
    selectedId = activeId
    mutableTimetable.value = storedTimetables.getValue(activeId)
    mutableTimetables.value = storedTimetables.toSummaries()
    mutableSelectedTimetableId.value = activeId
  }

  private fun loadLibrary(): LinkedHashMap<String, TimetableSnapshot> {
    val indexed = runCatching(::readIndexedLibrary).getOrNull()
    if (!indexed.isNullOrEmpty()) return indexed
    val legacy = runCatching { codec.decode(legacyFile.readText(Charsets.UTF_8)) }.getOrNull()
      ?: return linkedMapOf()
    val id = UUID.randomUUID().toString()
    return linkedMapOf(id to legacy).also { migrated ->
      runCatching {
        writeTimetable(id, legacy)
        writeIndex(id, migrated.keys)
      }
    }
  }

  private fun readIndexedLibrary(): LinkedHashMap<String, TimetableSnapshot> {
    if (!indexFile.isFile) return linkedMapOf()
    val index = libraryCodec.decode(indexFile.readText(Charsets.UTF_8))
    return index.entries.associateTo(linkedMapOf()) { entry ->
      entry.id to codec.decode(timetableFile(entry.id).readText(Charsets.UTF_8))
    }
  }

  private fun loadSelectedId(timetables: Map<String, TimetableSnapshot>): String? {
    if (timetables.isEmpty()) return null
    val indexed =
      runCatching { libraryCodec.decode(indexFile.readText(Charsets.UTF_8)).selectedId }.getOrNull()
    return indexed?.takeIf(timetables::containsKey) ?: timetables.keys.first()
  }

  private fun selectedSnapshot(): TimetableSnapshot? = selectedId?.let(storedTimetables::get)

  private fun writeTimetable(id: String, timetable: TimetableSnapshot) =
    writeAtomically(timetableFile(id), codec.encode(timetable))

  private fun writeIndex(selectedId: String, ids: Collection<String>) =
    writeAtomically(
      indexFile,
      libraryCodec.encode(
        TimetableLibraryDocument(
          selectedId = selectedId,
          entries = ids.map(::TimetableLibraryEntry),
        ),
      ),
    )

  private fun timetableFile(id: String) = File(libraryDirectory, "$id.json")

  private fun Map<String, TimetableSnapshot>.toSummaries(): List<TimetableSummary> =
    map { (id, timetable) -> TimetableSummary(id, timetable.term.name) }

  private fun writeAtomically(destination: File, value: String) {
    destination.parentFile?.mkdirs()
    val temporary = File(destination.parentFile, "${destination.name}.tmp")
    temporary.writeText(value, Charsets.UTF_8)
    try {
      Files.move(
        temporary.toPath(),
        destination.toPath(),
        StandardCopyOption.ATOMIC_MOVE,
        StandardCopyOption.REPLACE_EXISTING,
      )
    } catch (_: AtomicMoveNotSupportedException) {
      Files.move(temporary.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
  }
}

private const val LEGACY_FILE_NAME = "active-timetable.json"
private const val LIBRARY_DIRECTORY_NAME = "timetables"
private const val INDEX_FILE_NAME = "index.json"
