package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem

@Composable
fun TimetableGrid(
  periods: List<ClassPeriod>,
  items: List<WeekScheduleItem>,
  modifier: Modifier = Modifier,
) {
  LazyColumn(modifier = modifier.fillMaxWidth()) {
    items(periods, key = ClassPeriod::number) { period ->
      PeriodRow(period, items)
      when (period.number) {
        2 -> BreakRow("午休")
        4 -> BreakRow("晚休")
      }
    }
    item { Spacer(Modifier.height(12.dp)) }
  }
}
