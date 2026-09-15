package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.did2818.timetable.domain.usecase.ClassEdit
import com.did2818.timetable.presentation.timetable.toDraft

@Composable
internal fun CourseEditorDialog(
  title: String,
  initial: ClassEdit,
  totalWeeks: Int,
  onDismiss: () -> Unit,
  onSave: (ClassEdit) -> Unit,
  onDelete: (() -> Unit)? = null,
) {
  var draft by remember(initial) { mutableStateOf(initial.toDraft()) }
  val result = runCatching { draft.toEdit(totalWeeks) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        CourseEditorForm(draft, onChange = { draft = it })
        result.exceptionOrNull()?.message?.let { Text(it) }
      }
    },
    confirmButton = {
      TextButton(enabled = result.isSuccess, onClick = { onSave(result.getOrThrow()) }) {
        Text("保存")
      }
    },
    dismissButton = {
      Row {
        onDelete?.let { delete -> TextButton(onClick = delete) { Text("删除") } }
        TextButton(onClick = onDismiss) { Text("取消") }
      }
    },
  )
}
