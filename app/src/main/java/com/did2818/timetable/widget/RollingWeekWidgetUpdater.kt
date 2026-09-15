package com.did2818.timetable.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.domain.usecase.BuildRollingSchedule
import com.did2818.timetable.domain.usecase.ResolveVisibleDays
import java.time.LocalDate

class RollingWeekWidgetUpdater(
  private val context: Context,
  private val repository: TimetableRepository,
  private val buildSchedule: BuildRollingSchedule = BuildRollingSchedule(),
  private val resolveVisibleDays: ResolveVisibleDays = ResolveVisibleDays(),
  private val renderer: RollingWeekRemoteViewsRenderer = RollingWeekRemoteViewsRenderer(context),
) {
  private val widgetManager = AppWidgetManager.getInstance(context)

  fun update(
    widgetIds: IntArray,
    today: LocalDate = LocalDate.now(),
  ) {
    val timetable = repository.timetable.value
    val visibleDays = resolveVisibleDays(timetable).toSet()
    val schedule =
      buildSchedule(today, DAYS_TO_SHOW, timetable)
        .filter { it.date.dayOfWeek in visibleDays }
    val periods = if (schedule.isEmpty()) emptyList() else timetable.periods.filter { it.visible }
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
