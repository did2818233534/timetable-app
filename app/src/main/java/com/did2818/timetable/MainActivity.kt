package com.did2818.timetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.did2818.timetable.presentation.navigation.MainNavigation
import com.did2818.timetable.presentation.theme.TimetableAppTheme
import com.did2818.timetable.di.appContainer
import com.did2818.timetable.widget.RollingWeekWidgetUpdater

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      TimetableAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val container = appContainer
          MainNavigation(
            repository = container.timetableRepository,
            importer = container.timetableImporter,
            onImported = {
              RollingWeekWidgetUpdater(this, container.timetableRepository).updateAll()
            },
          )
        }
      }
    }
  }
}
