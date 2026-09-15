package com.did2818.timetable.presentation.timetable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.presentation.common.file.readUtf8Text
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
  Box(modifier = modifier.fillMaxSize()) {
    MainScreen(
      state = state,
      onPreviousWeek = viewModel::showPreviousWeek,
      onNextWeek = viewModel::showNextWeek,
      onImportClick = { filePicker.launch(arrayOf("application/json", "text/plain")) },
    )
    SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
  }
}
