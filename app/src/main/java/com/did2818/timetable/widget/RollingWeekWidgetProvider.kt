package com.did2818.timetable.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.did2818.timetable.MainActivity
import com.did2818.timetable.R
import com.did2818.timetable.data.sample.SampleTimetableData
import com.did2818.timetable.domain.model.Course
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RollingWeekWidgetProvider : AppWidgetProvider() {
  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray,
  ) {
    appWidgetIds.forEach { appWidgetId -> updateWidget(context, appWidgetManager, appWidgetId) }
  }

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    if (intent.action in dateChangeActions) {
      val manager = AppWidgetManager.getInstance(context)
      val ids = manager.getAppWidgetIds(ComponentName(context, RollingWeekWidgetProvider::class.java))
      onUpdate(context, manager, ids)
    }
  }

  companion object {
    fun updateWidget(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetId: Int,
      today: LocalDate = LocalDate.now(),
    ) {
      val schedule =
        buildRollingWidgetSchedule(
          startDate = today,
          term = SampleTimetableData.term,
          periods = SampleTimetableData.periods,
          courses = SampleTimetableData.courses,
          meetings = SampleTimetableData.meetings,
        )
      val views = RemoteViews(context.packageName, R.layout.widget_rolling_week)
      views.setTextViewText(R.id.widget_title, "未来 7 天")
      views.setTextViewText(
        R.id.widget_range,
        "${schedule.first().date.monthDay()}–${schedule.last().date.monthDay()}",
      )

      schedule.forEachIndexed { dayIndex, day ->
        views.setTextViewText(
          dayHeaderIds[dayIndex],
          "${day.date.dayOfWeek.shortChinese()}\n${day.date.dayOfMonth}",
        )
      }

      SampleTimetableData.periods.forEachIndexed { periodIndex, period ->
        views.setTextViewText(
          periodHeaderIds[periodIndex],
          "${period.number}\n${period.startTime.format(timeFormatter)}",
        )
        schedule.forEachIndexed { dayIndex, day ->
          val viewId = courseCellIds[periodIndex][dayIndex]
          bindCourseCell(context, views, viewId, day.cells[periodIndex])
        }
      }

      val launchIntent = Intent(context, MainActivity::class.java)
      val pendingIntent =
        PendingIntent.getActivity(
          context,
          0,
          launchIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
      views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun bindCourseCell(
      context: Context,
      views: RemoteViews,
      viewId: Int,
      cell: WidgetCourseCell?,
    ) {
      if (cell == null) {
        views.setTextViewText(viewId, "")
        views.setInt(viewId, "setBackgroundResource", R.drawable.widget_cell_empty)
        views.setTextColor(viewId, context.getColor(R.color.widget_text))
        views.setContentDescription(viewId, "无课程")
        return
      }

      val room = cell.meeting.classroom.substringAfterLast(' ').takeIf(String::isNotBlank)
      views.setTextViewText(
        viewId,
        listOfNotNull(cell.course.name, room).joinToString("\n"),
      )
      if (cell.isActive) {
        views.setInt(viewId, "setBackgroundResource", cell.course.backgroundResource())
        views.setTextColor(viewId, context.getColor(cell.course.textColorResource()))
      } else {
        views.setInt(viewId, "setBackgroundResource", R.drawable.widget_cell_inactive)
        views.setTextColor(viewId, context.getColor(R.color.widget_inactive_text))
      }
      val status = if (cell.isActive) "上课" else "本日不上课"
      views.setContentDescription(viewId, "${cell.course.name}，${cell.meeting.classroom}，$status")
    }

    private fun Course.backgroundResource(): Int =
      when (id.hashCode().mod(COURSE_PALETTE_SIZE)) {
        0 -> R.drawable.widget_cell_blue
        1 -> R.drawable.widget_cell_teal
        2 -> R.drawable.widget_cell_purple
        else -> R.drawable.widget_cell_orange
      }

    private fun Course.textColorResource(): Int =
      when (id.hashCode().mod(COURSE_PALETTE_SIZE)) {
        0 -> R.color.widget_blue_text
        1 -> R.color.widget_teal_text
        2 -> R.color.widget_purple_text
        else -> R.color.widget_orange_text
      }
  }
}

private val dateChangeActions =
  setOf(
    Intent.ACTION_DATE_CHANGED,
    Intent.ACTION_TIME_CHANGED,
    Intent.ACTION_TIMEZONE_CHANGED,
  )

private val dayHeaderIds =
  intArrayOf(
    R.id.widget_day_0,
    R.id.widget_day_1,
    R.id.widget_day_2,
    R.id.widget_day_3,
    R.id.widget_day_4,
    R.id.widget_day_5,
    R.id.widget_day_6,
  )

private val periodHeaderIds =
  intArrayOf(
    R.id.widget_period_0,
    R.id.widget_period_1,
    R.id.widget_period_2,
    R.id.widget_period_3,
    R.id.widget_period_4,
  )

private val courseCellIds =
  arrayOf(
    intArrayOf(
      R.id.widget_cell_0_0,
      R.id.widget_cell_0_1,
      R.id.widget_cell_0_2,
      R.id.widget_cell_0_3,
      R.id.widget_cell_0_4,
      R.id.widget_cell_0_5,
      R.id.widget_cell_0_6,
    ),
    intArrayOf(
      R.id.widget_cell_1_0,
      R.id.widget_cell_1_1,
      R.id.widget_cell_1_2,
      R.id.widget_cell_1_3,
      R.id.widget_cell_1_4,
      R.id.widget_cell_1_5,
      R.id.widget_cell_1_6,
    ),
    intArrayOf(
      R.id.widget_cell_2_0,
      R.id.widget_cell_2_1,
      R.id.widget_cell_2_2,
      R.id.widget_cell_2_3,
      R.id.widget_cell_2_4,
      R.id.widget_cell_2_5,
      R.id.widget_cell_2_6,
    ),
    intArrayOf(
      R.id.widget_cell_3_0,
      R.id.widget_cell_3_1,
      R.id.widget_cell_3_2,
      R.id.widget_cell_3_3,
      R.id.widget_cell_3_4,
      R.id.widget_cell_3_5,
      R.id.widget_cell_3_6,
    ),
    intArrayOf(
      R.id.widget_cell_4_0,
      R.id.widget_cell_4_1,
      R.id.widget_cell_4_2,
      R.id.widget_cell_4_3,
      R.id.widget_cell_4_4,
      R.id.widget_cell_4_5,
      R.id.widget_cell_4_6,
    ),
  )

private val monthDayFormatter = DateTimeFormatter.ofPattern("M/d")
private val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

private fun LocalDate.monthDay(): String = format(monthDayFormatter)

private fun DayOfWeek.shortChinese(): String =
  when (this) {
    DayOfWeek.MONDAY -> "一"
    DayOfWeek.TUESDAY -> "二"
    DayOfWeek.WEDNESDAY -> "三"
    DayOfWeek.THURSDAY -> "四"
    DayOfWeek.FRIDAY -> "五"
    DayOfWeek.SATURDAY -> "六"
    DayOfWeek.SUNDAY -> "日"
  }

private const val COURSE_PALETTE_SIZE = 4
