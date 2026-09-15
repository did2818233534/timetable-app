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
  var note by remember(initial) { mutableStateOf(initial.note) }
  var startWeek by remember(initial) { mutableStateOf(initial.weekPattern.startWeek.toString()) }
  var endWeek by remember(initial) { mutableStateOf(initial.weekPattern.endWeek.toString()) }
  var parity by remember(initial) { mutableStateOf(initial.weekPattern.parity) }
  var activeWeeksText by remember(initial) {
    mutableStateOf(initial.weekPattern.activeWeeks?.sorted()?.joinToString(",").orEmpty())
  }
  val first = startWeek.toIntOrNull()
  val last = endWeek.toIntOrNull()
  val activeWeeks = activeWeeksText.toWeekSet()
  val customWeeksValid = activeWeeksText.isBlank() || activeWeeks != null
  val valid =
    name.isNotBlank() && first != null && last != null && first in 1..totalWeeks &&
      last in first..totalWeeks && customWeeksValid && activeWeeks?.all { it in first..last } != false

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = androidx.compose.ui.Modifier.verticalScroll(rememberScrollState()),
      ) {
        OutlinedTextField(name, { name = it }, label = { Text("课程名称") }, singleLine = true)
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
          value = activeWeeksText,
          onValueChange = { activeWeeksText = it },
          label = { Text("指定周次（可选，如 1,2,5）") },
          supportingText = { Text("填写后按这些周次上课，不再按单双周推算") },
          isError = !customWeeksValid,
          singleLine = true,
        )
        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("备注（教室也填写在这里）") },
          minLines = 2,
          maxLines = 4,
        )
      }
    },
    confirmButton = {
      TextButton(
        enabled = valid,
        onClick = {
          onSave(
            ClassEdit(
              name,
              note,
              WeekPattern(first!!, last!!, parity, activeWeeks = activeWeeks),
            ),
          )
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

private fun String.toWeekSet(): Set<Int>? {
  if (isBlank()) return null
  val tokens = trim().split(Regex("[,，\\s]+"))
  val weeks = tokens.map { it.toIntOrNull() }
  return if (weeks.any { it == null }) null else weeks.filterNotNull().toSet()
}
