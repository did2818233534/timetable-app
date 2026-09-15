package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
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
import com.did2818.timetable.domain.model.MAX_REMINDER_MINUTES
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.model.ReminderSettings

@Composable
internal fun ReminderSettingsDialog(
  initial: ReminderSettings,
  onDismiss: () -> Unit,
  onSave: (ReminderSettings) -> Unit,
) {
  var scope by remember(initial) { mutableStateOf(initial.scope) }
  var minutes by remember(initial) { mutableStateOf(initial.minutesBefore.toString()) }
  var delivery by remember(initial) { mutableStateOf(initial.delivery) }
  val minutesValue = minutes.toIntOrNull()
  val valid = scope == ReminderScope.DISABLED || minutesValue in 0..MAX_REMINDER_MINUTES

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("全局提醒设置") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("默认提醒哪些课程")
        ReminderScope.entries.forEach { value ->
          FilterChip(
            selected = scope == value,
            onClick = { scope = value },
            label = { Text(value.displayName()) },
          )
        }
        if (scope != ReminderScope.DISABLED) {
          OutlinedTextField(
            value = minutes,
            onValueChange = { minutes = it.filter(Char::isDigit) },
            label = { Text("提前分钟数（0–180）") },
            isError = !valid,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          )
          ReminderDeliverySelector(delivery) { delivery = it }
          Text("闹钟方式会持续响铃，需在系统中允许通知和精确闹钟权限。")
        }
      }
    },
    confirmButton = {
      TextButton(
        enabled = valid,
        onClick = {
          onSave(ReminderSettings(scope, minutesValue ?: initial.minutesBefore, delivery))
        },
      ) { Text("保存") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
  )
}

private fun ReminderScope.displayName(): String =
  when (this) {
    ReminderScope.DISABLED -> "关闭全局提醒"
    ReminderScope.ALL_CLASSES -> "每节课都提醒"
    ReminderScope.FIRST_CLASS_OF_DAY -> "每天第一节课提醒"
  }
