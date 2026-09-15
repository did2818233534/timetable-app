package com.did2818.timetable.presentation.timetable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.domain.usecase.ClassEdit
import com.did2818.timetable.presentation.common.file.readUtf8Text
import com.did2818.timetable.presentation.timetable.components.CourseEditorDialog
import com.did2818.timetable.presentation.timetable.components.SlotCoursesDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainRoute(
  repository: TimetableRepository,
  importer: TimetableImporter,
  onImported: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(repository, importer, onImported) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val snackbar = remember { SnackbarHostState() }
  var editorTarget by remember { mutableStateOf<CourseEditorTarget?>(null) }
  var slotSelection by remember { mutableStateOf<SlotSelection?>(null) }
  var conflictWarning by remember { mutableStateOf<String?>(null) }
  val filePicker =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      uri?.let {
        scope.launch {
          runCatching {
              withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(it)?.use { stream -> stream.readUtf8Text() }
                  ?: error("无法打开所选文件")
              }
            }
            .onSuccess(viewModel::importDocument)
            .onFailure(viewModel::reportFileReadError)
        }
      }
    }

  LaunchedEffect(viewModel) { viewModel.messages.collect { snackbar.showSnackbar(it) } }
  LaunchedEffect(viewModel) { viewModel.conflictWarnings.collect { conflictWarning = it } }
  Box(modifier = modifier.fillMaxSize()) {
    MainScreen(
      state = state,
      onPreviousWeek = viewModel::showPreviousWeek,
      onNextWeek = viewModel::showNextWeek,
      onImportClick = { filePicker.launch(arrayOf("application/json", "text/plain")) },
      onOpenCell = { day, period, items ->
        when (items.size) {
          0 -> editorTarget = CourseEditorTarget.New(day, period)
          1 -> editorTarget = CourseEditorTarget.Existing(items.single(), period)
          else -> slotSelection = SlotSelection(day, period, items)
        }
      },
      onCopyItem = viewModel::copyMeeting,
      onPasteCell = viewModel::pasteMeeting,
    )
    SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
  }
  editorTarget?.let { target ->
    val existing = (target as? CourseEditorTarget.Existing)?.item
    CourseEditorDialog(
      title = "${target.day.displayName()} 第 ${target.period.number} 节",
      initial =
        existing?.let {
          ClassEdit(it.course.name, it.meeting.displayNote.ifBlank { it.course.note }, it.meeting.weekPattern)
        } ?: ClassEdit(
          name = "",
          note = "",
          weekPattern = WeekPattern(state.selectedWeek, state.totalWeeks, WeekParity.EVERY_WEEK),
        ),
      totalWeeks = state.totalWeeks,
      onDismiss = { editorTarget = null },
      onSave = { edit ->
        if (existing == null) viewModel.addMeeting(target.day, target.period, edit)
        else viewModel.updateMeeting(existing, edit)
        editorTarget = null
      },
      onDelete = existing?.let { item ->
        {
          viewModel.deleteMeeting(item)
          editorTarget = null
        }
      },
    )
  }
  slotSelection?.let { slot ->
    SlotCoursesDialog(
      title = "${slot.day.displayName()} 第 ${slot.period.number} 节的课程",
      items = slot.items,
      onSelect = { item ->
        slotSelection = null
        editorTarget = CourseEditorTarget.Existing(item, slot.period)
      },
      onAdd = {
        slotSelection = null
        editorTarget = CourseEditorTarget.New(slot.day, slot.period)
      },
      onDismiss = { slotSelection = null },
    )
  }
  conflictWarning?.let { warning ->
    AlertDialog(
      onDismissRequest = { conflictWarning = null },
      title = { Text("课程时间冲突") },
      text = { Text(warning) },
      confirmButton = { TextButton(onClick = { conflictWarning = null }) { Text("知道了") } },
    )
  }
}

private fun java.time.DayOfWeek.displayName(): String =
  listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")[value - 1]
