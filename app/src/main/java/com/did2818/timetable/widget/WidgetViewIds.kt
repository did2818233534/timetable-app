package com.did2818.timetable.widget

import com.did2818.timetable.R

/** Central mapping between the fixed RemoteViews grid and renderer coordinates. */
internal object WidgetViewIds {
  val dayHeaders =
    intArrayOf(
      R.id.widget_day_0,
      R.id.widget_day_1,
      R.id.widget_day_2,
      R.id.widget_day_3,
      R.id.widget_day_4,
      R.id.widget_day_5,
      R.id.widget_day_6,
    )

  val periodHeaders =
    intArrayOf(
      R.id.widget_period_0,
      R.id.widget_period_1,
      R.id.widget_period_2,
      R.id.widget_period_3,
      R.id.widget_period_4,
    )

  val courseCells =
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
}
