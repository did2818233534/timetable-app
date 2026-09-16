package com.did2818.timetable.presentation.timetable

import android.net.Uri
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.did2818.timetable.domain.repository.TimetableExporter
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun MainRoute(
  repository: TimetableRepository,
  importer: TimetableImporter,
  exporter: TimetableExporter,
  externalDocumentUri: Uri?,
  onExternalDocumentHandled: () -> Unit,
  onImported: () -> Unit,
  onReminderPermissionRequest: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel =
    viewModel { MainScreenViewModel(repository, importer, exporter, onImported) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbar = remember { SnackbarHostState() }
  var editorTarget by remember { mutableStateOf<CourseEditorTarget?>(null) }
  var slotSelection by remember { mutableStateOf<SlotSelection?>(null) }
  var conflictWarning by remember { mutableStateOf<String?>(null) }
  var newTimetableStep by remember { mutableStateOf(NewTimetableStep.CLOSED) }
  var settingsPage by remember { mutableStateOf(TimetableSettingsPage.CLOSED) }
  val fileActions =
    rememberTimetableFileActions(
      externalDocumentUri = externalDocumentUri,
      onExternalDocumentHandled = onExternalDocumentHandled,
      importDocument = viewModel::importDocument,
      exportDocument = viewModel::exportDocument,
      onExported = viewModel::reportExportSuccess,
      onFailure = viewModel::reportFileReadError,
    )

  LaunchedEffect(viewModel) { viewModel.messages.collect { snackbar.showSnackbar(it) } }
  LaunchedEffect(viewModel) { viewModel.conflictWarnings.collect { conflictWarning = it } }
  LaunchedEffect(viewModel) {
    viewModel.reminderPermissionRequests.collect { onReminderPermissionRequest() }
  }
  Box(modifier = modifier.fillMaxSize()) {
    MainScreen(
      state = state,
      stateForWeek = viewModel::stateForWeek,
      onSelectWeek = viewModel::selectWeek,
      onImportClick = fileActions.importFromPicker,
      onExportClick = { fileActions.exportToPicker(state.termName) },
      onNewClick = {
        newTimetableStep =
          if (state.hasTimetable) NewTimetableStep.CONFIRM_REPLACE else NewTimetableStep.EDIT
      },
      onSettingsClick = { settingsPage = TimetableSettingsPage.LAYOUT },
      onOpenCell = { day, period, items ->
        if (items.isEmpty()) editorTarget = CourseEditorTarget.New(day, period)
        else slotSelection = SlotSelection(day, period, items)
      },
      onCopyItem = viewModel::copyMeeting,
      onPasteCell = viewModel::pasteMeeting,
    )
    SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
  }
  NewTimetableFlow(
    step = newTimetableStep,
    defaultStartDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
    onStepChange = { newTimetableStep = it },
    onCreate = {
      viewModel.createTimetable(it)
      newTimetableStep = NewTimetableStep.CLOSED
    },
  )
  TimetableSettingsFlow(
    page = settingsPage,
    state = state,
    onPageChange = { settingsPage = it },
    onLayoutSave = viewModel::updateLayout,
    onReminderSave = viewModel::updateReminderSettings,
    onReminderPermissionRequest = onReminderPermissionRequest,
  )
  CourseEditingFlow(
    state = state,
    editorTarget = editorTarget,
    slotSelection = slotSelection,
    onEditorTargetChange = { editorTarget = it },
    onSlotSelectionChange = { slotSelection = it },
    onAdd = viewModel::addMeeting,
    onUpdate = viewModel::updateMeeting,
    onDelete = viewModel::deleteMeeting,
    onSplitDisplayChange = viewModel::updateSplitDisplay,
    onReminderPermissionRequest = onReminderPermissionRequest,
  )
  conflictWarning?.let { warning ->
    AlertDialog(
      onDismissRequest = { conflictWarning = null },
      title = { Text("课程时间冲突") },
      text = { Text(warning) },
      confirmButton = { TextButton(onClick = { conflictWarning = null }) { Text("知道了") } },
    )
  }
}
