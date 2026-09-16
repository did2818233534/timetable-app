package com.did2818.timetable.data.export.spreadsheet

import com.did2818.timetable.data.sample.SampleTimetableRepository
import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XlsxTimetableExporterTest {
  @Test
  fun export_createsValidWorkbookWithGridAndDetails() {
    val workbook = XlsxTimetableExporter().export(SampleTimetableRepository().timetable.value)
    val entries = unzip(workbook)

    assertEquals('P'.code.toByte(), workbook[0])
    assertEquals('K'.code.toByte(), workbook[1])
    assertTrue(REQUIRED_ENTRIES.all(entries::containsKey))
    entries.filterKeys { it.endsWith(".xml") }.values.forEach(::parseXml)

    val grid = entries.getValue("xl/worksheets/sheet1.xml").toString(Charsets.UTF_8)
    assertTrue(grid.contains("2026 秋季学期"))
    assertTrue(grid.contains("高等数学"))
    assertTrue(grid.contains("星期日"))
    assertTrue(grid.contains("orientation=\"landscape\""))
    assertTrue(grid.contains("mergeCell ref=\"A1:I1\""))

    val details = entries.getValue("xl/worksheets/sheet2.xml").toString(Charsets.UTF_8)
    assertTrue(details.contains("课程明细"))
    assertTrue(details.contains("周次规则"))
    assertTrue(details.contains("单周"))
    assertTrue(details.contains("autoFilter ref=\"A5:O9\""))

    val sample = File("build/test-artifacts/sample-timetable.xlsx")
    checkNotNull(sample.parentFile).mkdirs()
    sample.writeBytes(workbook)
  }

  private fun unzip(bytes: ByteArray): Map<String, ByteArray> =
    buildMap {
      ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
          put(entry.name, zip.readBytes())
          zip.closeEntry()
          entry = zip.nextEntry
        }
      }
    }

  private fun parseXml(bytes: ByteArray) {
    val factory = DocumentBuilderFactory.newInstance()
    factory.isNamespaceAware = true
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory.newDocumentBuilder().parse(ByteArrayInputStream(bytes))
  }

  private companion object {
    val REQUIRED_ENTRIES =
      setOf(
        "[Content_Types].xml",
        "_rels/.rels",
        "xl/workbook.xml",
        "xl/_rels/workbook.xml.rels",
        "xl/styles.xml",
        "xl/worksheets/sheet1.xml",
        "xl/worksheets/sheet2.xml",
      )
  }
}
