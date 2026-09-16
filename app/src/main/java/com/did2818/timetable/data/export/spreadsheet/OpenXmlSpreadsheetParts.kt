package com.did2818.timetable.data.export.spreadsheet

internal const val XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
internal const val SPREADSHEET_NAMESPACE =
  "http://schemas.openxmlformats.org/spreadsheetml/2006/main"
private const val RELATIONSHIP_NAMESPACE =
  "http://schemas.openxmlformats.org/officeDocument/2006/relationships"

internal fun contentTypes(sheetCount: Int): String =
  buildString {
    append(XML_HEADER)
    append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">")
    append("<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>")
    append("<Default Extension=\"xml\" ContentType=\"application/xml\"/>")
    append("<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>")
    append("<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>")
    append("<Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>")
    append("<Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>")
    repeat(sheetCount) { index ->
      append("<Override PartName=\"/xl/worksheets/sheet${index + 1}.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>")
    }
    append("</Types>")
  }

internal fun packageRelationships(): String =
  XML_HEADER +
    "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
    "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>" +
    "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/>" +
    "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/>" +
    "</Relationships>"

internal fun workbookXml(sheets: List<SpreadsheetSheet>): String =
  buildString {
    append(XML_HEADER)
    append("<workbook xmlns=\"$SPREADSHEET_NAMESPACE\" xmlns:r=\"$RELATIONSHIP_NAMESPACE\">")
    append("<bookViews><workbookView/></bookViews><sheets>")
    sheets.forEachIndexed { index, sheet ->
      append("<sheet name=\"${sheet.name.xmlText()}\" sheetId=\"${index + 1}\" r:id=\"rId${index + 1}\"/>")
    }
    append("</sheets></workbook>")
  }

internal fun workbookRelationships(sheetCount: Int): String =
  buildString {
    append(XML_HEADER)
    append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">")
    repeat(sheetCount) { index ->
      append("<Relationship Id=\"rId${index + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet${index + 1}.xml\"/>")
    }
    append("<Relationship Id=\"rId${sheetCount + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>")
    append("</Relationships>")
  }

internal fun coreProperties(): String =
  XML_HEADER +
    "<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" " +
    "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"><dc:creator>咕嘎课程表</dc:creator>" +
    "<dc:title>课程表</dc:title></cp:coreProperties>"

internal fun appProperties(sheetNames: List<String>): String =
  buildString {
    append(XML_HEADER)
    append("<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\" xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\">")
    append("<Application>咕嘎课程表</Application><TitlesOfParts><vt:vector size=\"${sheetNames.size}\" baseType=\"lpstr\">")
    sheetNames.forEach { append("<vt:lpstr>${it.xmlText()}</vt:lpstr>") }
    append("</vt:vector></TitlesOfParts></Properties>")
  }

internal fun stylesXml(): String = XML_HEADER + STYLES_BODY

private val STYLES_BODY =
  """
  <styleSheet xmlns="$SPREADSHEET_NAMESPACE">
    <numFmts count="2"><numFmt numFmtId="164" formatCode="hh:mm"/><numFmt numFmtId="165" formatCode="yyyy-mm-dd"/></numFmts>
    <fonts count="4">
      <font><sz val="10"/><name val="Arial"/><family val="2"/></font>
      <font><b/><color rgb="FFFFFFFF"/><sz val="10"/><name val="Arial"/><family val="2"/></font>
      <font><b/><color rgb="FF1F2937"/><sz val="14"/><name val="Arial"/><family val="2"/></font>
      <font><i/><color rgb="FF5F6B7A"/><sz val="10"/><name val="Arial"/><family val="2"/></font>
    </fonts>
    <fills count="4"><fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill><fill><patternFill patternType="solid"><fgColor rgb="FF314A66"/><bgColor indexed="64"/></patternFill></fill><fill><patternFill patternType="solid"><fgColor rgb="FFE8EEF5"/><bgColor indexed="64"/></patternFill></fill></fills>
    <borders count="2"><border/><border><left style="thin"><color rgb="FFD6DCE4"/></left><right style="thin"><color rgb="FFD6DCE4"/></right><top style="thin"><color rgb="FFD6DCE4"/></top><bottom style="thin"><color rgb="FFD6DCE4"/></bottom></border></borders>
    <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
    <cellXfs count="10">
      <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0" applyAlignment="1"><alignment vertical="center"/></xf>
      <xf numFmtId="0" fontId="2" fillId="0" borderId="0" xfId="0" applyFont="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
      <xf numFmtId="0" fontId="3" fillId="0" borderId="0" xfId="0" applyFont="1" applyAlignment="1"><alignment vertical="center"/></xf>
      <xf numFmtId="0" fontId="1" fillId="2" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center" wrapText="1"/></xf>
      <xf numFmtId="0" fontId="0" fillId="3" borderId="1" xfId="0" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center" wrapText="1"/></xf>
      <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1" applyAlignment="1"><alignment vertical="top" wrapText="1"/></xf>
      <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
      <xf numFmtId="165" fontId="0" fillId="0" borderId="1" xfId="0" applyNumberFormat="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
      <xf numFmtId="164" fontId="0" fillId="0" borderId="1" xfId="0" applyNumberFormat="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
      <xf numFmtId="0" fontId="0" fillId="3" borderId="0" xfId="0" applyFill="1" applyAlignment="1"><alignment vertical="center"/></xf>
    </cellXfs>
    <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
  </styleSheet>
  """.trimIndent()
