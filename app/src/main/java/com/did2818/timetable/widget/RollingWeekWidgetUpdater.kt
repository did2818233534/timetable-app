package com.did2818.timetable.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.domain.usecase.BuildRollingSchedule
import java.time.LocalDate

class RollingWeekWidgetUpdater(
  private val context: Context,
  private val repository: TimetableRepository,
  private val buildSchedule: BuildRollingSchedule = BuildRollingSchedule(),
  private val renderer: RollingWeekRemoteViewsRenderer = RollingWeekRemoteViewsRenderer(context),
) {
  private val widgetManager = AppWidgetManager.getInstance(context)

  fun update(
    widgetIds: IntArray,
    today: LocalDate = LocalDate.now(),
  ) {
    val timetable = repository.timetable.value
    val schedule =
      buildSchedule(today, DAYS_TO_SHOW, timetable)
        .filter { it.date.dayOfWeek in timetable.visibleDays }
    val periods = timetable.periods.filter { it.visible }
    widgetIds.forEach { widgetId ->
      widgetManager.updateAppWidget(widgetId, renderer.render(periods, schedule))
    }
  }

  fun updateAll(today: LocalDate = LocalDate.now()) {
    val provider = ComponentName(context, RollingWeekWidgetProvider::class.java)
    update(widgetManager.getAppWidgetIds(provider), today)
  }
}

private const val DAYS_TO_SHOW = 7
