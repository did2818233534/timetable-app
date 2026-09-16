package com.did2818.timetable.data.export.spreadsheet

internal fun String.xmlText(): String =
  buildString(length) {
    this@xmlText.forEach { character ->
      append(
        when (character) {
          '&' -> "&amp;"
          '<' -> "&lt;"
          '>' -> "&gt;"
          '"' -> "&quot;"
          '\'' -> "&apos;"
          else -> character
        },
      )
    }
  }

internal fun columnName(index: Int): String {
  require(index >= 0)
  var remaining = index + 1
  return buildString {
      while (remaining > 0) {
        val digit = (remaining - 1) % 26
        append(('A'.code + digit).toChar())
        remaining = (remaining - 1) / 26
      }
    }
    .reversed()
}
