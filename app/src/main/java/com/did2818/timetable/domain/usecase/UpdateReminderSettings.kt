package com.did2818.timetable.domain.usecase

import com.did2818.timetable.domain.model.ReminderSettings
import com.did2818.timetable.domain.model.TimetableSnapshot

class UpdateReminderSettings {
  operator fun invoke(
    timetable: TimetableSnapshot,
    settings: ReminderSettings,
  ): TimetableSnapshot = timetable.copy(reminderSettings = settings)
}
