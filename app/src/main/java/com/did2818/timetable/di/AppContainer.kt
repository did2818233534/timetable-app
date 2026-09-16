package com.did2818.timetable.di

import android.content.Context
import com.did2818.timetable.TimetableApplication
import com.did2818.timetable.data.export.spreadsheet.XlsxTimetableExporter
import com.did2818.timetable.data.importexport.JsonTimetableImporter
import com.did2818.timetable.data.importexport.JsonTimetableExporter
import com.did2818.timetable.data.importexport.TimetableJsonCodec
import com.did2818.timetable.data.local.FileTimetableRepository
import com.did2818.timetable.data.defaults.emptyTimetable
import com.did2818.timetable.domain.repository.TimetableExporter
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.domain.repository.TimetableSpreadsheetExporter
import com.did2818.timetable.reminder.ReminderScheduler

class AppContainer(context: Context) {
  private val codec = TimetableJsonCodec()

  val timetableRepository: TimetableRepository =
    FileTimetableRepository(context.filesDir, emptyTimetable(), codec)
  val timetableImporter: TimetableImporter = JsonTimetableImporter(timetableRepository, codec)
  val timetableExporter: TimetableExporter = JsonTimetableExporter(codec)
  val timetableSpreadsheetExporter: TimetableSpreadsheetExporter = XlsxTimetableExporter()
  val reminderScheduler: ReminderScheduler = ReminderScheduler(context)
}

val Context.appContainer: AppContainer
  get() = (applicationContext as TimetableApplication).container
