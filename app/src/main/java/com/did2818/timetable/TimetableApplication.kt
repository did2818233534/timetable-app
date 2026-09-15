package com.did2818.timetable

import android.app.Application
import com.did2818.timetable.di.AppContainer

class TimetableApplication : Application() {
  val container: AppContainer by lazy { AppContainer(this) }
}
