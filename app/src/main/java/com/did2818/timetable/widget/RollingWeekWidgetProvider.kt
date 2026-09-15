package com.did2818.timetable.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.did2818.timetable.di.appContainer

class RollingWeekWidgetProvider : AppWidgetProvider() {
  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray,
  ) {
    context.widgetUpdater().update(appWidgetIds)
  }

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    if (intent.action in dateChangeActions) context.widgetUpdater().updateAll()
  }
}

private fun Context.widgetUpdater() =
  RollingWeekWidgetUpdater(
    context = this,
    repository = appContainer.timetableRepository,
  )

private val dateChangeActions =
  setOf(
    Intent.ACTION_DATE_CHANGED,
    Intent.ACTION_TIME_CHANGED,
    Intent.ACTION_TIMEZONE_CHANGED,
  )
