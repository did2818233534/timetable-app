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
  val exportJsonToPicker: (String) -> Unit,
  val exportExcelToPicker: (String) -> Unit,
)

@Composable
internal fun rememberTimetableFileActions(
  externalDocumentUri: Uri?,
  onExternalDocumentHandled: () -> Unit,
  importDocument: (String) -> Unit,
  exportJsonDocument: () -> String,
  exportExcelDocument: () -> ByteArray,
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
  fun writeDocument(uri: Uri, document: () -> ByteArray) {
    scope.launch {
      runCatching {
          val bytes = document()
          withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(bytes) }
              ?: error("无法写入所选文件")
          }
        }
        .onSuccess { onExported() }
        .onFailure(onFailure)
    }
  }

  val jsonExportPicker =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(JSON_MIME)) { uri ->
      uri?.let { writeDocument(it) { exportJsonDocument().toByteArray(Charsets.UTF_8) } }
    }
  val excelExportPicker =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(XLSX_MIME)) { uri ->
      uri?.let { writeDocument(it, exportExcelDocument) }
    }

  LaunchedEffect(externalDocumentUri) {
    externalDocumentUri?.let { importUri(it, onExternalDocumentHandled) }
  }

  return TimetableFileActions(
    importFromPicker = {
      importPicker.launch(arrayOf(JSON_MIME, "application/octet-stream", "text/json", "text/plain"))
    },
    exportJsonToPicker = { name -> jsonExportPicker.launch("${name.safeFileName()}.timetable.json") },
    exportExcelToPicker = { name -> excelExportPicker.launch("${name.safeFileName()}.xlsx") },
  )
}

private fun String.safeFileName(): String =
  trim().ifBlank { "课程表" }.replace(Regex("""[\\/:*?"<>|]"""), "_")

private const val JSON_MIME = "application/json"
private const val XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
