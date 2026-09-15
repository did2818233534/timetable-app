package com.did2818.timetable.presentation.timetable

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.did2818.timetable.presentation.common.file.readUtf8Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal data class TimetableFileActions(
  val importFromPicker: () -> Unit,
  val exportToPicker: (String) -> Unit,
)

@Composable
internal fun rememberTimetableFileActions(
  externalDocumentUri: Uri?,
  onExternalDocumentHandled: () -> Unit,
  importDocument: (String) -> Unit,
  exportDocument: () -> String,
  onExported: () -> Unit,
  onFailure: (Throwable) -> Unit,
): TimetableFileActions {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  fun importUri(uri: Uri, onFinished: () -> Unit = {}) {
    scope.launch {
      runCatching {
          withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readUtf8Text() }
              ?: error("无法打开所选文件")
          }
        }
        .onSuccess(importDocument)
        .onFailure(onFailure)
      onFinished()
    }
  }

  val importPicker =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      uri?.let(::importUri)
    }
  val exportPicker =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(JSON_MIME)) { uri ->
      uri?.let {
        scope.launch {
          runCatching {
              val document = exportDocument()
              withContext(Dispatchers.IO) {
                context.contentResolver.openOutputStream(it, "wt")?.bufferedWriter()?.use { writer ->
                  writer.write(document)
                } ?: error("无法写入所选文件")
              }
            }
            .onSuccess { onExported() }
            .onFailure(onFailure)
        }
      }
    }

  LaunchedEffect(externalDocumentUri) {
    externalDocumentUri?.let { importUri(it, onExternalDocumentHandled) }
  }

  return TimetableFileActions(
    importFromPicker = {
      importPicker.launch(arrayOf(JSON_MIME, "application/octet-stream", "text/json", "text/plain"))
    },
    exportToPicker = { name -> exportPicker.launch("${name.safeFileName()}.timetable.json") },
  )
}

private fun String.safeFileName(): String =
  trim().ifBlank { "课程表" }.replace(Regex("""[\\/:*?"<>|]"""), "_")

private const val JSON_MIME = "application/json"
