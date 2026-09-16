package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.TimetableSummary

@Composable
internal fun TimetableActionsMenu(
  timetables: List<TimetableSummary>,
  selectedTimetableId: String?,
  onImportClick: () -> Unit,
  onExportClick: () -> Unit,
  onNewClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onTimetableSelected: (String) -> Unit,
  exportEnabled: Boolean,
  settingsEnabled: Boolean,
) {
  var menuExpanded by remember { mutableStateOf(false) }
  var switcherVisible by remember { mutableStateOf(false) }
  Box {
    IconButton(
      onClick = { menuExpanded = true },
      modifier = Modifier.testTag("timetable-menu-button"),
    ) { HamburgerIcon() }
    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
      MenuItem("切换课程表", enabled = timetables.isNotEmpty()) {
        menuExpanded = false
        switcherVisible = true
      }
      MenuItem("导入课程表") {
        menuExpanded = false
        onImportClick()
      }
      MenuItem("导出课程表", enabled = exportEnabled) {
        menuExpanded = false
        onExportClick()
      }
      MenuItem("新建课程表") {
        menuExpanded = false
        onNewClick()
      }
      MenuItem("设置", enabled = settingsEnabled) {
        menuExpanded = false
        onSettingsClick()
      }
    }
  }
  if (switcherVisible) {
    TimetableSwitchDialog(
      timetables = timetables,
      selectedTimetableId = selectedTimetableId,
      onDismiss = { switcherVisible = false },
      onSelect = {
        switcherVisible = false
        onTimetableSelected(it)
      },
    )
  }
}

@Composable
private fun MenuItem(text: String, enabled: Boolean = true, onClick: () -> Unit) {
  DropdownMenuItem(text = { Text(text) }, onClick = onClick, enabled = enabled)
}

@Composable
private fun HamburgerIcon() {
  val color = MaterialTheme.colorScheme.onSurface
  Canvas(
    Modifier
      .size(24.dp)
      .semantics { contentDescription = "课表菜单" },
  ) {
    listOf(0.28f, 0.5f, 0.72f).forEach { verticalFraction ->
      val y = size.height * verticalFraction
      drawLine(
        color = color,
        start = Offset(size.width * 0.18f, y),
        end = Offset(size.width * 0.82f, y),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round,
      )
    }
  }
}

@Composable
private fun TimetableSwitchDialog(
  timetables: List<TimetableSummary>,
  selectedTimetableId: String?,
  onDismiss: () -> Unit,
  onSelect: (String) -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("切换课程表") },
    text = {
      Column(Modifier.fillMaxWidth().heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
        timetables.forEach { timetable ->
          TextButton(onClick = { onSelect(timetable.id) }, modifier = Modifier.fillMaxWidth()) {
            Text(if (timetable.id == selectedTimetableId) "✓ ${timetable.name}" else timetable.name)
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
  )
}
