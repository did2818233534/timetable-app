package com.did2818.timetable.presentation.timetable

import androidx.compose.runtime.Composable
import com.did2818.timetable.domain.usecase.NewTimetableSpec
import com.did2818.timetable.presentation.timetable.components.NewTimetableDialog
import java.time.LocalDate

internal enum class NewTimetableStep {
  CLOSED,
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
    NewTimetableStep.EDIT ->
      NewTimetableDialog(
        defaultStartDate = defaultStartDate,
        onCreate = onCreate,
        onDismiss = { onStepChange(NewTimetableStep.CLOSED) },
      )
  }
}
