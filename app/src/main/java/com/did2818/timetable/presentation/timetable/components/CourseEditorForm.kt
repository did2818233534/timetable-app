package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.presentation.timetable.CourseEditorDraft

@Composable
internal fun CourseEditorForm(
  draft: CourseEditorDraft,
  onChange: (CourseEditorDraft) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    OutlinedTextField(
      draft.name,
      { onChange(draft.copy(name = it)) },
      label = { Text("课程名称") },
      singleLine = true,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      WeekField("开始周", draft.startWeek, { onChange(draft.copy(startWeek = it)) }, Modifier.weight(1f))
      WeekField("结束周", draft.endWeek, { onChange(draft.copy(endWeek = it)) }, Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      WeekParity.entries.forEach { parity ->
        FilterChip(
          selected = draft.parity == parity,
          onClick = { onChange(draft.copy(parity = parity)) },
          label = { Text(parity.displayName()) },
        )
      }
    }
    OutlinedTextField(
      value = draft.activeWeeks,
      onValueChange = { onChange(draft.copy(activeWeeks = it)) },
      label = { Text("指定周次（可选，如 1,2,5）") },
      supportingText = { Text("填写后按这些周次上课，不再按单双周推算") },
      isError = !draft.customWeeksValid,
      singleLine = true,
    )
    OutlinedTextField(
      value = draft.note,
      onValueChange = { onChange(draft.copy(note = it)) },
      label = { Text("备注（教室也填写在这里）") },
      minLines = 2,
      maxLines = 4,
    )
    MeetingReminderEditor(
      mode = draft.reminderMode,
      minutesBefore = draft.reminderMinutes,
      delivery = draft.reminderDelivery,
      onModeChange = { onChange(draft.copy(reminderMode = it)) },
      onMinutesChange = { onChange(draft.copy(reminderMinutes = it)) },
      onDeliveryChange = { onChange(draft.copy(reminderDelivery = it)) },
    )
  }
}

@Composable
private fun WeekField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier,
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
