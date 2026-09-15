package com.did2818.timetable.widget

import com.did2818.timetable.R
import com.did2818.timetable.domain.model.Course

internal data class WidgetCourseStyle(
  val backgroundResource: Int,
  val textColorResource: Int,
)

internal fun Course.widgetStyle(): WidgetCourseStyle =
  when (id.hashCode().mod(PALETTE_SIZE)) {
    0 -> WidgetCourseStyle(R.drawable.widget_cell_blue, R.color.widget_blue_text)
    1 -> WidgetCourseStyle(R.drawable.widget_cell_teal, R.color.widget_teal_text)
    2 -> WidgetCourseStyle(R.drawable.widget_cell_purple, R.color.widget_purple_text)
    else -> WidgetCourseStyle(R.drawable.widget_cell_orange, R.color.widget_orange_text)
  }

private const val PALETTE_SIZE = 4
