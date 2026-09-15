package com.did2818.timetable.presentation.timetable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.did2818.timetable.domain.usecase.NewTimetableSpec
import com.did2818.timetable.presentation.timetable.components.NewTimetableDialog
import java.time.LocalDate

internal enum class NewTimetableStep {
  CLOSED,
  CONFIRM_REPLACE,
  EDIT,
}

@Composable
internal fun NewTimetableFlow(
  step: NewTimetableStep,
  defaultStartDate: LocalDate,
  onStepChange: (NewTimetableStep) -> Unit,
  onCreate: (NewTimetableSpec) -> Unit,
) {
  when (step) {
    NewTimetableStep.CLOSED -> Unit
    NewTimetableStep.CONFIRM_REPLACE ->
      AlertDialog(
        onDismissRequest = { onStepChange(NewTimetableStep.CLOSED) },
        title = { Text("新建课程表？") },
        text = { Text("新建后会替换当前课程表。如需保留，请先导出备份。") },
        confirmButton = {
          TextButton(onClick = { onStepChange(NewTimetableStep.EDIT) }) { Text("继续新建") }
        },
        dismissButton = {
          TextButton(onClick = { onStepChange(NewTimetableStep.CLOSED) }) { Text("取消") }
        },
      )
    NewTimetableStep.EDIT ->
      NewTimetableDialog(
        defaultStartDate = defaultStartDate,
        onCreate = onCreate,
        onDismiss = { onStepChange(NewTimetableStep.CLOSED) },
      )
  }
}
