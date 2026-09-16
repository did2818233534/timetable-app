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
import com.did2818.timetable.domain.model.SplitDisplaySlot
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek

@Composable
fun TimetableGrid(
  periods: List<ClassPeriod>,
  visibleDays: List<DayOfWeek>,
  items: List<WeekScheduleItem>,
  selectedWeek: Int,
  totalWeeks: Int,
  splitDisplaySlots: Set<SplitDisplaySlot>,
  onOpenCell: (DayOfWeek, ClassPeriod, List<WeekScheduleItem>) -> Unit,
  onCopyItem: (WeekScheduleItem) -> Unit,
  onPasteCell: (DayOfWeek, ClassPeriod) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(modifier = modifier.fillMaxWidth()) {
    items(periods, key = ClassPeriod::number) { period ->
      PeriodRow(
        period,
        items,
        visibleDays,
        selectedWeek,
        totalWeeks,
        splitDisplaySlots,
        onOpenCell,
        onCopyItem,
        onPasteCell,
      )
      period.breakAfter?.let { BreakRow(it) }
    }
    item { Spacer(Modifier.height(12.dp)) }
  }
}
