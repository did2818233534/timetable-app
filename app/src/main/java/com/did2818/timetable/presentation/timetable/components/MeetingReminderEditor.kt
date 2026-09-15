package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.presentation.timetable.MeetingReminderMode

@Composable
internal fun MeetingReminderEditor(
  mode: MeetingReminderMode,
  minutesBefore: String,
  delivery: ReminderDelivery,
  onModeChange: (MeetingReminderMode) -> Unit,
  onMinutesChange: (String) -> Unit,
  onDeliveryChange: (ReminderDelivery) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text("本节课提醒")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      MeetingReminderMode.entries.forEach { value ->
        FilterChip(
          selected = mode == value,
          onClick = { onModeChange(value) },
          label = { Text(value.displayName()) },
        )
      }
    }
    if (mode == MeetingReminderMode.ENABLED) {
      OutlinedTextField(
        value = minutesBefore,
        onValueChange = { onMinutesChange(it.filter(Char::isDigit)) },
        label = { Text("提前分钟数（0–180）") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      )
      ReminderDeliverySelector(delivery, onDeliveryChange)
    }
  }
}

private fun MeetingReminderMode.displayName(): String =
  when (this) {
    MeetingReminderMode.INHERIT -> "继承"
    MeetingReminderMode.ENABLED -> "开启"
    MeetingReminderMode.DISABLED -> "关闭"
  }
