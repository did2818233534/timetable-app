package com.did2818.timetable.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.did2818.timetable.MainActivity
import com.did2818.timetable.R
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.DaySchedule
import com.did2818.timetable.domain.model.WeekScheduleItem

class RollingWeekRemoteViewsRenderer(private val context: Context) {
  fun render(
    periods: List<ClassPeriod>,
    days: List<DaySchedule>,
  ): RemoteViews =
    RemoteViews(context.packageName, R.layout.widget_rolling_week).apply {
      bindHeader(days)
      bindDayColumns(days)
      bindPeriodRows(periods, days)
      setOnClickPendingIntent(R.id.widget_root, launchAppIntent())
    }

  private fun RemoteViews.bindHeader(days: List<DaySchedule>) {
    setTextViewText(R.id.widget_title, "未来 7 天")
    val range =
      if (days.isEmpty()) "暂无课程"
      else "${days.first().date.widgetDateText()}–${days.last().date.widgetDateText()}"
    setTextViewText(R.id.widget_range, range)
  }

  private fun RemoteViews.bindDayColumns(days: List<DaySchedule>) {
    removeAllViews(R.id.widget_day_container)
    days.forEach { day ->
      val column = RemoteViews(context.packageName, R.layout.widget_day_cell)
      column.setTextViewText(
        R.id.widget_dynamic_text,
        "${day.date.dayOfWeek.widgetDayText()}\n${day.date.dayOfMonth}",
      )
      addView(R.id.widget_day_container, column)
    }
  }

  private fun RemoteViews.bindPeriodRows(
    periods: List<ClassPeriod>,
    days: List<DaySchedule>,
  ) {
    removeAllViews(R.id.widget_period_container)
    periods.forEachIndexed { periodIndex, period ->
      val row = RemoteViews(context.packageName, R.layout.widget_period_row)
      row.setTextViewText(
        R.id.widget_dynamic_period,
        "${period.number}\n${period.startTime.widgetTimeText()}",
      )
      days.forEach { day ->
        val cell = RemoteViews(context.packageName, R.layout.widget_course_cell)
        cell.bindCourseCell(day.periodItems[periodIndex])
        row.addView(R.id.widget_dynamic_cells, cell)
      }
      addView(R.id.widget_period_container, row)
    }
  }

  private fun RemoteViews.bindCourseCell(items: List<WeekScheduleItem>) {
    removeAllViews(R.id.widget_course_entries)
    val visibleItems = items.take(MAX_COURSES_PER_CELL)
    if (visibleItems.isEmpty()) {
      val entry = RemoteViews(context.packageName, R.layout.widget_course_entry)
      entry.bindEmptyCourseEntry()
      addView(R.id.widget_course_entries, entry)
      setContentDescription(R.id.widget_course_entries, "无课程")
      return
    }
    visibleItems.forEach { item ->
      val entry = RemoteViews(context.packageName, R.layout.widget_course_entry)
      entry.bindCourseEntry(item)
      addView(R.id.widget_course_entries, entry)
    }
    val extra =
      (items.size - visibleItems.size).takeIf { it > 0 }?.let { "，另有 $it 门课程" }.orEmpty()
    setContentDescription(
      R.id.widget_course_entries,
      visibleItems.joinToString("；") { it.accessibilityText() } + extra,
    )
  }

  private fun RemoteViews.bindEmptyCourseEntry() {
    val viewId = R.id.widget_dynamic_text
    setTextViewText(viewId, "")
    setInt(viewId, "setBackgroundResource", R.drawable.widget_cell_empty)
    setTextColor(viewId, context.getColor(R.color.widget_text))
  }

  private fun RemoteViews.bindCourseEntry(item: WeekScheduleItem) {
    val viewId = R.id.widget_dynamic_text
    val note = item.meeting.displayNote.ifBlank { item.course.note }.takeIf(String::isNotBlank)
    setTextViewText(viewId, listOfNotNull(item.course.name, note).joinToString("\n"))
    if (item.isActive) bindActiveStyle(viewId, item) else bindInactiveStyle(viewId)
  }

  private fun RemoteViews.bindActiveStyle(viewId: Int, item: WeekScheduleItem) {
    val style = item.course.widgetStyle()
    setInt(viewId, "setBackgroundResource", style.backgroundResource)
    setTextColor(viewId, context.getColor(style.textColorResource))
  }

  private fun RemoteViews.bindInactiveStyle(viewId: Int) {
    setInt(viewId, "setBackgroundResource", R.drawable.widget_cell_inactive)
    setTextColor(viewId, context.getColor(R.color.widget_inactive_text))
  }

  private fun launchAppIntent(): PendingIntent =
    PendingIntent.getActivity(
      context,
      0,
      Intent(context, MainActivity::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

private fun WeekScheduleItem.accessibilityText(): String {
  val note = meeting.displayNote.ifBlank { course.note }
  val status = if (isActive) "上课" else "本日不上课"
  return "${course.name}，$note，$status"
}

private const val MAX_COURSES_PER_CELL = 2
