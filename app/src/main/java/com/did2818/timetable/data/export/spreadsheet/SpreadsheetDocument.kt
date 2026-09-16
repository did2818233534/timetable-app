package com.did2818.timetable.data.export.spreadsheet

internal data class SpreadsheetDocument(val sheets: List<SpreadsheetSheet>)

internal data class SpreadsheetSheet(
  val name: String,
  val rows: List<SpreadsheetRow>,
  val columnWidths: List<Double>,
  val mergedRanges: List<String> = emptyList(),
  val freezeRows: Int = 0,
  val freezeColumns: Int = 0,
  val autoFilterRange: String? = null,
  val landscape: Boolean = false,
)

internal data class SpreadsheetRow(
  val cells: List<SpreadsheetCell>,
  val height: Double? = null,
)

internal data class SpreadsheetCell(
  val text: String? = null,
  val number: Double? = null,
  val style: SpreadsheetCellStyle = SpreadsheetCellStyle.BODY,
) {
  init {
    require(text == null || number == null) { "A spreadsheet cell cannot contain text and a number" }
  }

  companion object {
    fun text(value: String, style: SpreadsheetCellStyle = SpreadsheetCellStyle.BODY) =
      SpreadsheetCell(text = value, style = style)

    fun number(value: Number, style: SpreadsheetCellStyle = SpreadsheetCellStyle.NUMBER) =
      SpreadsheetCell(number = value.toDouble(), style = style)

    fun blank() = SpreadsheetCell()
  }
}

internal enum class SpreadsheetCellStyle(val index: Int) {
  BODY(0),
  TITLE(1),
  SUBTITLE(2),
  HEADER(3),
  ROW_HEADER(4),
  TABLE_TEXT(5),
  NUMBER(6),
  DATE(7),
  TIME(8),
  SECTION(9),
}
