package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.presentation.timetable.compactWeekLabel

@Composable
internal fun SlotCoursesDialog(
  title: String,
  items: List<WeekScheduleItem>,
  onSelect: (WeekScheduleItem) -> Unit,
  onAdd: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = {
      Column {
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
