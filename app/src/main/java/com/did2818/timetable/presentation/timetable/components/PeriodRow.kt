package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.SplitDisplaySlot
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.usecase.SelectSlotDisplays
import com.did2818.timetable.presentation.common.format.timeText
import java.time.DayOfWeek

@Composable
internal fun PeriodRow(
  period: ClassPeriod,
  items: List<WeekScheduleItem>,
  visibleDays: List<DayOfWeek>,
  selectedWeek: Int,
  totalWeeks: Int,
  splitDisplaySlots: Set<SplitDisplaySlot>,
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
      val displayLimit =
        if (SplitDisplaySlot(day, period.number) in splitDisplaySlots) SPLIT_DISPLAY_LIMIT else 1
      val display = SelectSlotDisplays()(candidates, selectedWeek, totalWeeks, displayLimit)
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
                display.items.firstOrNull()?.let(onCopyItem) ?: onPasteCell(day, period)
              },
            )
            .padding(2.dp),
      ) {
        Column(
          verticalArrangement = Arrangement.spacedBy(2.dp),
          modifier = Modifier.fillMaxSize(),
        ) {
          val split = display.items.size > 1
          display.items.forEachIndexed { index, item ->
            CourseCell(
              item = item,
              hasConflict = display.hasConflict && index == 0,
              compact = split,
              modifier =
                Modifier
                  .then(if (split) Modifier.weight(1f) else Modifier.fillMaxSize())
                  .fillMaxWidth()
                  .testTag("slot-course-${item.meeting.id}"),
            )
          }
        }
        val hiddenCount = candidates.size - display.items.size
        if (hiddenCount > 0) MoreCoursesBadge(hiddenCount, Modifier.align(Alignment.BottomEnd))
      }
    }
  }
}

@Composable
private fun MoreCoursesBadge(count: Int, modifier: Modifier = Modifier) {
  Text(
    text = "+$count",
    color = MaterialTheme.colorScheme.onPrimary,
    fontSize = 8.sp,
    fontWeight = FontWeight.Bold,
    modifier =
      modifier
        .background(MaterialTheme.colorScheme.primary, CircleShape)
        .padding(horizontal = 4.dp, vertical = 1.dp),
  )
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

private const val SPLIT_DISPLAY_LIMIT = 2
