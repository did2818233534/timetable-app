package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.usecase.ClassEdit

@Composable
internal fun CourseEditorDialog(
  title: String,
  initial: ClassEdit,
  totalWeeks: Int,
  onDismiss: () -> Unit,
  onSave: (ClassEdit) -> Unit,
  onDelete: (() -> Unit)? = null,
) {
  var name by remember(initial) { mutableStateOf(initial.name) }
  var classroom by remember(initial) { mutableStateOf(initial.classroom) }
  var note by remember(initial) { mutableStateOf(initial.note) }
  var startWeek by remember(initial) { mutableStateOf(initial.weekPattern.startWeek.toString()) }
  var endWeek by remember(initial) { mutableStateOf(initial.weekPattern.endWeek.toString()) }
  var parity by remember(initial) { mutableStateOf(initial.weekPattern.parity) }
  val first = startWeek.toIntOrNull()
  val last = endWeek.toIntOrNull()
  val valid = name.isNotBlank() && first != null && last != null && first in 1..totalWeeks && last in first..totalWeeks

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = androidx.compose.ui.Modifier.verticalScroll(rememberScrollState()),
      ) {
        OutlinedTextField(name, { name = it }, label = { Text("课程名称") }, singleLine = true)
        OutlinedTextField(classroom, { classroom = it }, label = { Text("教室") }, singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          WeekField("开始周", startWeek, { startWeek = it }, androidx.compose.ui.Modifier.weight(1f))
          WeekField("结束周", endWeek, { endWeek = it }, androidx.compose.ui.Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          WeekParity.entries.forEach { value ->
            FilterChip(
              selected = parity == value,
              onClick = { parity = value },
              label = { Text(value.displayName()) },
            )
          }
        }
        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("备注") },
          minLines = 2,
          maxLines = 4,
        )
      }
    },
    confirmButton = {
      TextButton(
        enabled = valid,
        onClick = {
          onSave(ClassEdit(name, classroom, note, WeekPattern(first!!, last!!, parity)))
        },
      ) { Text("保存") }
    },
    dismissButton = {
      Row {
        onDelete?.let { delete -> TextButton(onClick = delete) { Text("删除") } }
        TextButton(onClick = onDismiss) { Text("取消") }
      }
    },
  )
}

@Composable
private fun WeekField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: androidx.compose.ui.Modifier,
) {
  OutlinedTextField(
    value = value,
    onValueChange = { onValueChange(it.filter(Char::isDigit)) },
    label = { Text(label) },
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    modifier = modifier,
  )
}

private fun WeekParity.displayName(): String =
  when (this) {
    WeekParity.EVERY_WEEK -> "每周"
    WeekParity.ODD_WEEKS -> "单周"
    WeekParity.EVEN_WEEKS -> "双周"
  }
