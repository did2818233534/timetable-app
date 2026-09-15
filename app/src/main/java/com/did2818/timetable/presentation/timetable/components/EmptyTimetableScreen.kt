package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyTimetableScreen(
  onImportClick: () -> Unit,
  onNewClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier.fillMaxSize()) {
    TimetableToolbar(
      termName = "咕嘎课程表",
      onImportClick = onImportClick,
      onExportClick = {},
      onNewClick = onNewClick,
      onSettingsClick = {},
      exportEnabled = false,
      settingsEnabled = false,
    )
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize().padding(bottom = 72.dp),
    ) {
      FloatingActionButton(onClick = onNewClick, modifier = Modifier.testTag("new-timetable")) {
        Text("+", fontSize = 36.sp)
      }
      Text(
        "新建课程表",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 14.dp),
      )
    }
  }
}
