package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.did2818.timetable.presentation.timetable.TimetablePeriodDraft

@Composable
internal fun TimetablePeriodSettingsRow(
  number: Int,
  period: TimetablePeriodDraft,
  onChange: (TimetablePeriodDraft) -> Unit,
  onDelete: () -> Unit,
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text("第 $number 节", modifier = Modifier.weight(1f))
      Checkbox(
        checked = period.visible,
        onCheckedChange = { onChange(period.copy(visible = it)) },
      )
      Text("显示")
      TextButton(onClick = onDelete) { Text("删除") }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      TimeField(
        period.startTime,
        { onChange(period.copy(startTime = it)) },
        "上课",
        Modifier.weight(1f),
      )
      Text("–")
      TimeField(
        period.endTime,
        { onChange(period.copy(endTime = it)) },
        "下课",
        Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun TimeField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    placeholder = { Text("08:00") },
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    modifier = modifier,
  )
}
