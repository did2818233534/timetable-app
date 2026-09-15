package com.did2818.timetable.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.did2818.timetable.domain.repository.TimetableExporter
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.presentation.timetable.MainRoute

@Composable
fun MainNavigation(
  repository: TimetableRepository,
  importer: TimetableImporter,
  exporter: TimetableExporter,
  externalDocumentUri: Uri?,
  onExternalDocumentHandled: () -> Unit,
  onImported: () -> Unit,
) {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainRoute(
            repository = repository,
            importer = importer,
            exporter = exporter,
            externalDocumentUri = externalDocumentUri,
            onExternalDocumentHandled = onExternalDocumentHandled,
            onImported = onImported,
            modifier = Modifier.safeDrawingPadding(),
          )
        }
      },
  )
}
