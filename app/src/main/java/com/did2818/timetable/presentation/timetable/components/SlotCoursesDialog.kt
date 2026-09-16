package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.presentation.timetable.compactWeekLabel

@Composable
internal fun SlotCoursesDialog(
  title: String,
  items: List<WeekScheduleItem>,
  splitDisplay: Boolean,
  onSplitDisplayChange: (Boolean) -> Unit,
  onSelect: (WeekScheduleItem) -> Unit,
  onAdd: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (items.size > 1) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
          ) {
            Column(Modifier.weight(1f)) {
              Text("上下分栏显示两门课程")
              Text(
                "关闭时只显示当前或下一门课程",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Switch(
              checked = splitDisplay,
              onCheckedChange = onSplitDisplayChange,
              modifier = Modifier.testTag("split-display-switch"),
            )
          }
          HorizontalDivider()
        }
        items.forEachIndexed { index, item ->
          TextButton(onClick = { onSelect(item) }, modifier = Modifier.fillMaxWidth()) {
            Text(item.summary())
          }
          if (index < items.lastIndex) HorizontalDivider()
        }
      }
    },
    confirmButton = { TextButton(onClick = onAdd) { Text("在此格新增") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
  )
}

private fun WeekScheduleItem.summary(): String {
  val note = meeting.displayNote.ifBlank { course.note }
  return listOf(course.name, compactWeekLabel(), note).filter(String::isNotBlank).joinToString(" · ")
}
