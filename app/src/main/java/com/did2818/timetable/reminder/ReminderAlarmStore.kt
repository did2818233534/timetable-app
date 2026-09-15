package com.did2818.timetable.reminder

import android.content.Context
import androidx.core.content.edit

internal class ReminderAlarmStore(context: Context) {
  private val preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

  fun read(): Set<String> = preferences.getStringSet(KEY_URIS, emptySet()).orEmpty().toSet()

  fun replace(uris: Set<String>) {
    preferences.edit { putStringSet(KEY_URIS, uris) }
  }
}

private const val STORE_NAME = "scheduled-reminders"
private const val KEY_URIS = "alarm-uris"
