package com.did2818.timetable.data.export.spreadsheet

import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.repository.TimetableSpreadsheetExporter

class XlsxTimetableExporter : TimetableSpreadsheetExporter {
  private val mapper = TimetableWorkbookMapper()
  private val writer = OpenXmlSpreadsheetWriter()

  override fun export(timetable: TimetableSnapshot): ByteArray = writer.write(mapper.map(timetable))
}
