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
      bindPeriods(periods, days)
      setOnClickPendingIntent(R.id.widget_root, launchAppIntent())
    }

  private fun RemoteViews.bindHeader(days: List<DaySchedule>) {
    setTextViewText(R.id.widget_title, "未来 7 天")
    setTextViewText(
      R.id.widget_range,
      "${days.first().date.widgetDateText()}–${days.last().date.widgetDateText()}",
    )
    days.forEachIndexed { index, day ->
      setTextViewText(
        WidgetViewIds.dayHeaders[index],
        "${day.date.dayOfWeek.widgetDayText()}\n${day.date.dayOfMonth}",
      )
    }
  }

  private fun RemoteViews.bindPeriods(
    periods: List<ClassPeriod>,
    days: List<DaySchedule>,
  ) {
    periods.forEachIndexed { periodIndex, period ->
      setTextViewText(
        WidgetViewIds.periodHeaders[periodIndex],
        "${period.number}\n${period.startTime.widgetTimeText()}",
      )
      days.forEachIndexed { dayIndex, day ->
        bindCourseCell(
          WidgetViewIds.courseCells[periodIndex][dayIndex],
          day.periodItems[periodIndex],
        )
      }
    }
  }

  private fun RemoteViews.bindCourseCell(viewId: Int, item: WeekScheduleItem?) {
    if (item == null) {
      setTextViewText(viewId, "")
      setInt(viewId, "setBackgroundResource", R.drawable.widget_cell_empty)
      setTextColor(viewId, context.getColor(R.color.widget_text))
      setContentDescription(viewId, "无课程")
      return
    }

    val room = item.meeting.classroom.substringAfterLast(' ').takeIf(String::isNotBlank)
    setTextViewText(viewId, listOfNotNull(item.course.name, room).joinToString("\n"))
    if (item.isActive) bindActiveStyle(viewId, item) else bindInactiveStyle(viewId)
    val status = if (item.isActive) "上课" else "本日不上课"
    setContentDescription(viewId, "${item.course.name}，${item.meeting.classroom}，$status")
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
