package com.did2818.timetable.di

import android.content.Context
import com.did2818.timetable.TimetableApplication
import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.repository.TimetableRepository

class AppContainer {
  val timetableRepository: TimetableRepository = SampleTimetableRepository()
}

val Context.appContainer: AppContainer
  get() = (applicationContext as TimetableApplication).container
