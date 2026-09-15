package com.did2818.timetable.presentation.timetable

import androidx.compose.runtime.Composable
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.model.ReminderSettings
import com.did2818.timetable.domain.usecase.TimetableLayoutEdit
import com.did2818.timetable.presentation.timetable.components.ReminderSettingsDialog
import com.did2818.timetable.presentation.timetable.components.TimetableLayoutSettingsDialog

internal enum class TimetableSettingsPage {
  CLOSED,
  LAYOUT,
  REMINDERS,
}

@Composable
internal fun TimetableSettingsFlow(
  page: TimetableSettingsPage,
  state: MainScreenUiState,
  onPageChange: (TimetableSettingsPage) -> Unit,
  onLayoutSave: (TimetableLayoutEdit, () -> Unit) -> Unit,
  onReminderSave: (ReminderSettings, () -> Unit) -> Unit,
  onReminderPermissionRequest: () -> Unit,
) {
  when (page) {
    TimetableSettingsPage.CLOSED -> Unit
    TimetableSettingsPage.LAYOUT ->
      TimetableLayoutSettingsDialog(
        periods = state.allPeriods,
        configuredDays = state.configuredDays,
        initialHideEmptyDays = state.hideEmptyDays,
        onDismiss = { onPageChange(TimetableSettingsPage.CLOSED) },
        onSave = { edit ->
          onLayoutSave(edit) { onPageChange(TimetableSettingsPage.CLOSED) }
        },
        onReminderSettingsClick = { onPageChange(TimetableSettingsPage.REMINDERS) },
      )
    TimetableSettingsPage.REMINDERS ->
      ReminderSettingsDialog(
        initial = state.reminderSettings,
        onDismiss = { onPageChange(TimetableSettingsPage.CLOSED) },
        onSave = { settings ->
          onReminderSave(settings) {
            onPageChange(TimetableSettingsPage.CLOSED)
            if (settings.scope != ReminderScope.DISABLED) onReminderPermissionRequest()
          }
        },
      )
  }
}
