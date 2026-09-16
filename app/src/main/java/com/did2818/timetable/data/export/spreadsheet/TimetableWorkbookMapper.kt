package com.did2818.timetable.data.export.spreadsheet

import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.SplitDisplaySlot
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.usecase.ResolveVisibleDays
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

internal class TimetableWorkbookMapper(
  private val resolveVisibleDays: ResolveVisibleDays = ResolveVisibleDays(),
) {
  fun map(timetable: TimetableSnapshot): SpreadsheetDocument =
    SpreadsheetDocument(listOf(gridSheet(timetable), detailsSheet(timetable)))

  private fun gridSheet(timetable: TimetableSnapshot): SpreadsheetSheet {
    val days = resolveVisibleDays(timetable)
    val periods = timetable.periods.filter { it.visible }
    val courses = timetable.courses.associateBy(Course::id)
    val columnCount = 2 + days.size
    val rows =
      buildList {
        add(row(text("${timetable.term.name}课表", SpreadsheetCellStyle.TITLE), height = 26.0))
        add(
          row(
            text(
              "${timetable.term.startDate} 至 ${timetable.term.endDate} · 共 ${timetable.term.totalWeeks} 周",
              SpreadsheetCellStyle.SUBTITLE,
            ),
            height = 20.0,
          ),
        )
        add(row())
        add(
          SpreadsheetRow(
            listOf(header("时间"), header("节次")) + days.map { header(it.chineseName()) },
            height = 24.0,
          ),
        )
        periods.forEach { period ->
          val cells =
            listOf(
              rowHeader("${period.startTime}\n${period.endTime}"),
              rowHeader(period.number.toString()),
            ) +
              days.map { day ->
                val meetings =
                  timetable.meetings
                    .filter { it.dayOfWeek == day && it.startTime == period.startTime }
                    .sortedWith(compareBy({ it.startTime }, { courses[it.courseId]?.name.orEmpty() }))
                tableText(meetings.joinToString("\n\n") { it.gridText(courses) })
              }
          add(SpreadsheetRow(cells, height = 74.0))
        }
      }
    val lastColumn = columnName(columnCount - 1)
    return SpreadsheetSheet(
      name = "课程表",
      rows = rows,
      columnWidths = listOf(13.0, 7.0) + List(days.size) { 20.0 },
      mergedRanges = listOf("A1:${lastColumn}1", "A2:${lastColumn}2"),
      freezeRows = 4,
      freezeColumns = 2,
      landscape = true,
    )
  }

  private fun detailsSheet(timetable: TimetableSnapshot): SpreadsheetSheet {
    val courses = timetable.courses.associateBy(Course::id)
    val sortedMeetings =
      timetable.meetings.sortedWith(
        compareBy<ClassMeeting>({ it.dayOfWeek.value }, { it.startTime }, { courses[it.courseId]?.name }),
      )
    val rows =
      buildList {
        add(row(text("课程明细", SpreadsheetCellStyle.TITLE), height = 26.0))
        add(termSettingsRow(timetable))
        add(row())
        add(row(text("每行代表一个上课时间；JSON 文件仍是应用重新导入的完整格式。", SpreadsheetCellStyle.SUBTITLE)))
        add(SpreadsheetRow(DETAIL_HEADERS.map(::header), height = 30.0))
        sortedMeetings.forEach { meeting -> add(meetingRow(timetable, courses, meeting)) }
        add(row())
        add(row(text("节次设置", SpreadsheetCellStyle.SECTION), height = 22.0))
        add(SpreadsheetRow(PERIOD_HEADERS.map(::header), height = 24.0))
        timetable.periods.sortedBy { it.number }.forEach { period ->
          add(
            SpreadsheetRow(
              listOf(
                number(period.number),
                time(period.startTime),
                time(period.endTime),
                tableText(if (period.visible) "显示" else "隐藏"),
                tableText(period.breakAfter.orEmpty()),
              ),
            ),
          )
        }
      }
    val detailLastRow = 5 + sortedMeetings.size
    return SpreadsheetSheet(
      name = "课程明细",
      rows = rows,
      columnWidths = DETAIL_WIDTHS,
      mergedRanges = listOf("A1:O1", "A4:O4"),
      freezeRows = 5,
      autoFilterRange = "A5:O$detailLastRow",
      landscape = true,
    )
  }

  private fun termSettingsRow(timetable: TimetableSnapshot): SpreadsheetRow =
    SpreadsheetRow(
      listOf(
        rowHeader("学期"),
        tableText(timetable.term.name),
        rowHeader("第一周周一"),
        date(timetable.term.startDate),
        rowHeader("总周数"),
        number(timetable.term.totalWeeks),
        rowHeader("显示星期"),
        tableText(timetable.visibleDays.sortedBy { it.value }.joinToString("、") { it.chineseName() }),
        rowHeader("隐藏无课日"),
        tableText(if (timetable.hideEmptyDays) "是" else "否"),
      ),
      height = 24.0,
    )

  private fun meetingRow(
    timetable: TimetableSnapshot,
    courses: Map<String, Course>,
    meeting: ClassMeeting,
  ): SpreadsheetRow {
    val course = courses.getValue(meeting.courseId)
    val period = timetable.periods.firstOrNull { it.startTime == meeting.startTime }
    val split = period?.let { SplitDisplaySlot(meeting.dayOfWeek, it.number) } in timetable.splitDisplaySlots
    return SpreadsheetRow(
      listOf(
        tableText(course.name),
        tableText(meeting.dayOfWeek.chineseName()),
        period?.let { number(it.number) } ?: SpreadsheetCell.blank(),
        time(meeting.startTime),
        time(meeting.endTime),
        number(meeting.weekPattern.startWeek),
        number(meeting.weekPattern.endWeek),
        tableText(meeting.weekPattern.ruleName()),
        tableText(meeting.weekPattern.excludedWeeks.sorted().joinToString("、")),
        tableText(meeting.weekPattern.activeWeeks?.sorted()?.joinToString("、").orEmpty()),
        tableText(course.teacher),
        tableText(course.note),
        tableText(meeting.displayNote),
        tableText(if (split) "上下分格" else "单课显示"),
        tableText(meeting.reminderOverride.summary()),
      ),
    )
  }

  private fun ClassMeeting.gridText(courses: Map<String, Course>): String {
    val course = courses.getValue(courseId)
    val notes = listOf(displayNote, course.teacher, weekPattern.summary()).filter(String::isNotBlank)
    return (listOf(course.name) + notes).joinToString("\n")
  }
}

private fun row(vararg cells: SpreadsheetCell, height: Double? = null) = SpreadsheetRow(cells.toList(), height)
private fun text(value: String, style: SpreadsheetCellStyle) = SpreadsheetCell.text(value, style)
private fun header(value: String) = SpreadsheetCell.text(value, SpreadsheetCellStyle.HEADER)
private fun rowHeader(value: String) = SpreadsheetCell.text(value, SpreadsheetCellStyle.ROW_HEADER)
private fun tableText(value: String) = SpreadsheetCell.text(value, SpreadsheetCellStyle.TABLE_TEXT)
private fun number(value: Number) = SpreadsheetCell.number(value)
private fun date(value: LocalDate) = SpreadsheetCell.number(value.excelSerial(), SpreadsheetCellStyle.DATE)
private fun time(value: LocalTime) = SpreadsheetCell.number(value.toSecondOfDay() / 86_400.0, SpreadsheetCellStyle.TIME)
private fun LocalDate.excelSerial(): Double = ChronoUnit.DAYS.between(EXCEL_EPOCH, this).toDouble()

private val EXCEL_EPOCH = LocalDate.of(1899, 12, 30)
private val DETAIL_HEADERS =
  listOf("课程名称", "星期", "节次", "上课时间", "下课时间", "开始周", "结束周", "周次规则", "排除周次", "指定周次", "教师", "课程备注", "本次备注", "格子显示", "提醒")
private val DETAIL_WIDTHS =
  listOf(24.0, 10.0, 8.0, 11.0, 11.0, 9.0, 9.0, 11.0, 13.0, 13.0, 16.0, 20.0, 22.0, 12.0, 15.0)
private val PERIOD_HEADERS = listOf("节次", "开始时间", "结束时间", "是否显示", "课间标签")
