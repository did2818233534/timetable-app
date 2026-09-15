package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TimetableToolbar(
  termName: String,
  onImportClick: () -> Unit,
  onExportClick: () -> Unit,
  onNewClick: () -> Unit,
  onSettingsClick: () -> Unit,
  exportEnabled: Boolean = true,
  settingsEnabled: Boolean = true,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth().height(48.dp).padding(start = 12.dp, end = 4.dp),
  ) {
    Text(
      text = termName,
      style = MaterialTheme.typography.titleSmall,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f),
    )
    ToolbarAction("导入", onImportClick)
    ToolbarAction("导出", onExportClick, exportEnabled)
    ToolbarAction("新建", onNewClick)
    ToolbarAction("设置", onSettingsClick, settingsEnabled)
  }
}

@Composable
private fun ToolbarAction(text: String, onClick: () -> Unit, enabled: Boolean = true) {
  TextButton(
    onClick = onClick,
    enabled = enabled,
    contentPadding = PaddingValues(horizontal = 7.dp),
    modifier = Modifier.defaultMinSize(minWidth = 0.dp),
  ) { Text(text) }
}
