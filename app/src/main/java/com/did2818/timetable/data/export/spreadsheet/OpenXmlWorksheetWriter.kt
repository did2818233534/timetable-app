package com.did2818.timetable.data.export.spreadsheet

internal object OpenXmlWorksheetWriter {
  fun write(sheet: SpreadsheetSheet): String =
    buildString {
      append(XML_HEADER)
      append("<worksheet xmlns=\"$SPREADSHEET_NAMESPACE\">")
      if (sheet.landscape) append("<sheetPr><pageSetUpPr fitToPage=\"1\"/></sheetPr>")
      appendDimension(sheet)
      appendSheetViews(sheet)
      append("<sheetFormatPr defaultRowHeight=\"18\"/>")
      appendColumns(sheet.columnWidths)
      appendRows(sheet.rows)
      sheet.autoFilterRange?.let { append("<autoFilter ref=\"${it.xmlText()}\"/>") }
      appendMergedRanges(sheet.mergedRanges)
      append("<pageMargins left=\"0.3\" right=\"0.3\" top=\"0.5\" bottom=\"0.5\" header=\"0.2\" footer=\"0.2\"/>")
      if (sheet.landscape) {
        append("<pageSetup paperSize=\"9\" orientation=\"landscape\" fitToWidth=\"1\" fitToHeight=\"0\"/>")
      }
      append("</worksheet>")
    }

  private fun StringBuilder.appendDimension(sheet: SpreadsheetSheet) {
    val lastColumn = columnName((sheet.rows.maxOfOrNull { it.cells.size } ?: 1) - 1)
    val lastRow = sheet.rows.size.coerceAtLeast(1)
    append("<dimension ref=\"A1:$lastColumn$lastRow\"/>")
  }

  private fun StringBuilder.appendSheetViews(sheet: SpreadsheetSheet) {
    append("<sheetViews><sheetView showGridLines=\"0\" workbookViewId=\"0\">")
    if (sheet.freezeRows > 0 || sheet.freezeColumns > 0) {
      val topLeft = "${columnName(sheet.freezeColumns)}${sheet.freezeRows + 1}"
      append("<pane")
      if (sheet.freezeColumns > 0) append(" xSplit=\"${sheet.freezeColumns}\"")
      if (sheet.freezeRows > 0) append(" ySplit=\"${sheet.freezeRows}\"")
      append(" topLeftCell=\"$topLeft\" activePane=\"bottomRight\" state=\"frozen\"/>")
    }
    append("</sheetView></sheetViews>")
  }

  private fun StringBuilder.appendColumns(widths: List<Double>) {
    if (widths.isEmpty()) return
    append("<cols>")
    widths.forEachIndexed { index, width ->
      val column = index + 1
      append("<col min=\"$column\" max=\"$column\" width=\"$width\" customWidth=\"1\"/>")
    }
    append("</cols>")
  }

  private fun StringBuilder.appendRows(rows: List<SpreadsheetRow>) {
    append("<sheetData>")
    rows.forEachIndexed { rowIndex, row ->
      val number = rowIndex + 1
      append("<row r=\"$number\"")
      row.height?.let { append(" ht=\"$it\" customHeight=\"1\"") }
      append(">")
      row.cells.forEachIndexed { columnIndex, cell -> appendCell(number, columnIndex, cell) }
      append("</row>")
    }
    append("</sheetData>")
  }

  private fun StringBuilder.appendCell(row: Int, column: Int, cell: SpreadsheetCell) {
    val reference = "${columnName(column)}$row"
    when {
      cell.text != null ->
        append(
          "<c r=\"$reference\" s=\"${cell.style.index}\" t=\"inlineStr\"><is>" +
            "<t xml:space=\"preserve\">${cell.text.xmlText()}</t></is></c>",
        )
      cell.number != null ->
        append("<c r=\"$reference\" s=\"${cell.style.index}\"><v>${cell.number}</v></c>")
      cell.style != SpreadsheetCellStyle.BODY ->
        append("<c r=\"$reference\" s=\"${cell.style.index}\"/>")
    }
  }

  private fun StringBuilder.appendMergedRanges(ranges: List<String>) {
    if (ranges.isEmpty()) return
    append("<mergeCells count=\"${ranges.size}\">")
    ranges.forEach { append("<mergeCell ref=\"${it.xmlText()}\"/>") }
    append("</mergeCells>")
  }
}
