package com.did2818.timetable.data.export.spreadsheet

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal class OpenXmlSpreadsheetWriter {
  fun write(document: SpreadsheetDocument): ByteArray {
    require(document.sheets.isNotEmpty()) { "Spreadsheet must contain at least one sheet" }
    return ByteArrayOutputStream().use { output ->
      ZipOutputStream(output).use { zip ->
        zip.writeEntry("[Content_Types].xml", contentTypes(document.sheets.size))
        zip.writeEntry("_rels/.rels", packageRelationships())
        zip.writeEntry("docProps/core.xml", coreProperties())
        zip.writeEntry("docProps/app.xml", appProperties(document.sheets.map { it.name }))
        zip.writeEntry("xl/workbook.xml", workbookXml(document.sheets))
        zip.writeEntry("xl/_rels/workbook.xml.rels", workbookRelationships(document.sheets.size))
        zip.writeEntry("xl/styles.xml", stylesXml())
        document.sheets.forEachIndexed { index, sheet ->
          zip.writeEntry("xl/worksheets/sheet${index + 1}.xml", OpenXmlWorksheetWriter.write(sheet))
        }
      }
      output.toByteArray()
    }
  }

  private fun ZipOutputStream.writeEntry(path: String, contents: String) {
    putNextEntry(ZipEntry(path).apply { time = 0L })
    write(contents.toByteArray(Charsets.UTF_8))
    closeEntry()
  }
}
