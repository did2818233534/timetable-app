package com.did2818.timetable

import android.app.Application
import com.did2818.timetable.di.AppContainer

class TimetableApplication : Application() {
  val container: AppContainer by lazy { AppContainer(this) }

  override fun onCreate() {
    super.onCreate()
    container.reminderScheduler.reschedule(container.timetableRepository.timetable.value)
  }
}
