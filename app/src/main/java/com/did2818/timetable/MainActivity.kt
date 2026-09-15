package com.did2818.timetable

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.did2818.timetable.presentation.navigation.MainNavigation
import com.did2818.timetable.presentation.theme.TimetableAppTheme
import com.did2818.timetable.di.appContainer
import com.did2818.timetable.widget.RollingWeekWidgetUpdater
import com.did2818.timetable.reminder.ReminderPermissionRequester

class MainActivity : ComponentActivity() {
  private var incomingDocumentUri by mutableStateOf<Uri?>(null)
  private val reminderPermissionRequester = ReminderPermissionRequester(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    incomingDocumentUri = intent.timetableDocumentUri()

    enableEdgeToEdge()
    setContent {
      TimetableAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val container = appContainer
          MainNavigation(
            repository = container.timetableRepository,
            importer = container.timetableImporter,
            exporter = container.timetableExporter,
            externalDocumentUri = incomingDocumentUri,
            onExternalDocumentHandled = { incomingDocumentUri = null },
            onImported = {
              RollingWeekWidgetUpdater(this, container.timetableRepository).updateAll()
              container.reminderScheduler.reschedule(container.timetableRepository.timetable.value)
            },
            onReminderPermissionRequest = reminderPermissionRequester::request,
          )
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    incomingDocumentUri = intent.timetableDocumentUri()
  }

  override fun onResume() {
    super.onResume()
    val container = appContainer
    container.reminderScheduler.reschedule(container.timetableRepository.timetable.value)
  }
}
