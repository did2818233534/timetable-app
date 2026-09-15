package com.did2818.timetable.di

import android.content.Context
import com.did2818.timetable.TimetableApplication
import com.did2818.timetable.data.importexport.JsonTimetableImporter
import com.did2818.timetable.data.importexport.TimetableJsonCodec
import com.did2818.timetable.data.local.FileTimetableRepository
import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository

class AppContainer(context: Context) {
  private val codec = TimetableJsonCodec()
  private val fallback = SampleTimetableRepository().timetable.value

  val timetableRepository: TimetableRepository =
    FileTimetableRepository(context.filesDir, fallback, codec)
  val timetableImporter: TimetableImporter = JsonTimetableImporter(timetableRepository, codec)
}

val Context.appContainer: AppContainer
  get() = (applicationContext as TimetableApplication).container
