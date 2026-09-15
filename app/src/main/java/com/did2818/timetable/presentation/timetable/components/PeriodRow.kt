package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.usecase.SelectSlotDisplay
import com.did2818.timetable.presentation.common.format.timeText
import java.time.DayOfWeek

@Composable
internal fun PeriodRow(
  period: ClassPeriod,
  items: List<WeekScheduleItem>,
  visibleDays: List<DayOfWeek>,
  selectedWeek: Int,
  totalWeeks: Int,
  onOpenCell: (DayOfWeek, ClassPeriod, List<WeekScheduleItem>) -> Unit,
  onCopyItem: (WeekScheduleItem) -> Unit,
  onPasteCell: (DayOfWeek, ClassPeriod) -> Unit,
) {
  val gridColor = MaterialTheme.colorScheme.outlineVariant
  Row(modifier = Modifier.fillMaxWidth().height(PeriodRowHeight)) {
    PeriodLabel(
      period,
      Modifier.width(TimeColumnWidth).fillMaxHeight().border(GridLineWidth, gridColor),
    )
    visibleDays.forEach { day ->
      val candidates = items.filter { it.matches(day, period) }
      val display = SelectSlotDisplay()(candidates, selectedWeek, totalWeeks)
      Box(
        modifier =
          Modifier
            .weight(1f)
            .fillMaxHeight()
            .border(GridLineWidth, gridColor)
            .testTag("slot-${day.name}-${period.number}")
            .combinedClickable(
              onClick = { onOpenCell(day, period, candidates) },
              onLongClick = {
                display?.let { onCopyItem(it.item) } ?: onPasteCell(day, period)
              },
            )
            .padding(2.dp),
      ) {
        display?.let { CourseCell(it.item, it.hasConflict) }
      }
    }
  }
}

@Composable
private fun PeriodLabel(period: ClassPeriod, modifier: Modifier = Modifier) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
    Spacer(Modifier.weight(1f))
    Text(period.number.toString(), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    Text(period.startTime.timeText(), style = MaterialTheme.typography.labelSmall)
    Text(period.endTime.timeText(), style = MaterialTheme.typography.labelSmall)
    Spacer(Modifier.weight(1f))
  }
}

@Composable
internal fun BreakRow(label: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier =
      Modifier
        .fillMaxWidth()
        .height(BreakRowHeight)
        .background(MaterialTheme.colorScheme.surfaceVariant),
  ) {
    Spacer(Modifier.width(TimeColumnWidth))
    Text(
      text = label,
      textAlign = TextAlign.Center,
      fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.weight(1f),
    )
  }
}

private fun WeekScheduleItem.matches(day: DayOfWeek, period: ClassPeriod): Boolean =
  meeting.dayOfWeek == day && meeting.startTime == period.startTime
