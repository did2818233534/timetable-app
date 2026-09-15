package com.did2818.timetable.presentation.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.did2818.timetable.data.sample.SampleTimetableData
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.usecase.BuildWeekSchedule
import com.did2818.timetable.presentation.theme.TimetableAppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val gridLineWidth = 0.5.dp
private val timeColumnWidth = 54.dp
private val periodRowHeight = 116.dp

@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel() },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  MainScreen(
    state = state,
    onPreviousWeek = viewModel::showPreviousWeek,
    onNextWeek = viewModel::showNextWeek,
    modifier = modifier,
  )
}

@Composable
internal fun MainScreen(
  state: MainScreenUiState,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxSize()) {
    WeekNavigation(
      state = state,
      onPreviousWeek = onPreviousWeek,
      onNextWeek = onNextWeek,
    )
    DayHeader(weekStart = state.weekStart)
    TimetableGrid(
      periods = state.periods,
      items = state.items,
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun WeekNavigation(
  state: MainScreenUiState,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth().height(76.dp),
  ) {
    TextButton(onClick = onPreviousWeek, enabled = state.selectedWeek > 1) {
      Text("‹", fontSize = 36.sp, fontWeight = FontWeight.Light)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "第 ${state.selectedWeek} 周",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = "${state.weekStart.shortDate()} – ${state.weekEnd.shortDate()}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    TextButton(onClick = onNextWeek, enabled = state.selectedWeek < state.totalWeeks) {
      Text("›", fontSize = 36.sp, fontWeight = FontWeight.Light)
    }
  }
}

@Composable
private fun DayHeader(weekStart: LocalDate) {
  val gridColor = MaterialTheme.colorScheme.outlineVariant
  Row(modifier = Modifier.fillMaxWidth().height(58.dp)) {
    Box(
      modifier = Modifier.width(timeColumnWidth).fillMaxHeight().border(gridLineWidth, gridColor),
    )
    DayOfWeek.entries.forEachIndexed { index, day ->
      val date = weekStart.plusDays(index.toLong())
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f).fillMaxHeight().border(gridLineWidth, gridColor),
      ) {
        Text(
          text = day.chineseName(),
          fontSize = 12.sp,
          lineHeight = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = date.monthDay(),
          fontSize = 10.sp,
          lineHeight = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun TimetableGrid(
  periods: List<ClassPeriod>,
  items: List<WeekScheduleItem>,
  modifier: Modifier = Modifier,
) {
  LazyColumn(modifier = modifier.fillMaxWidth()) {
    items(periods, key = ClassPeriod::number) { period ->
      PeriodRow(
        period = period,
        items = items,
      )
      when (period.number) {
        2 -> BreakRow("午休")
        4 -> BreakRow("晚休")
      }
    }
    item { Spacer(Modifier.height(12.dp)) }
  }
}

@Composable
private fun PeriodRow(
  period: ClassPeriod,
  items: List<WeekScheduleItem>,
) {
  val gridColor = MaterialTheme.colorScheme.outlineVariant
  Row(modifier = Modifier.fillMaxWidth().height(periodRowHeight)) {
    PeriodLabel(
      period = period,
      modifier =
        Modifier
          .width(timeColumnWidth)
          .fillMaxHeight()
          .border(gridLineWidth, gridColor),
    )
    DayOfWeek.entries.forEach { day ->
      val item =
        items.firstOrNull {
          it.meeting.dayOfWeek == day && it.meeting.startTime == period.startTime
        }
      Box(
        modifier =
          Modifier
            .weight(1f)
            .fillMaxHeight()
            .border(gridLineWidth, gridColor)
            .padding(2.dp),
      ) {
        if (item != null) CourseCell(item)
      }
    }
  }
}

@Composable
private fun PeriodLabel(
  period: ClassPeriod,
  modifier: Modifier = Modifier,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier,
  ) {
    Text(
      text = period.number.toString(),
      fontSize = 20.sp,
      fontWeight = FontWeight.SemiBold,
    )
    Text(
      text = period.startTime.timeText(),
      fontSize = 9.sp,
      lineHeight = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = period.endTime.timeText(),
      fontSize = 9.sp,
      lineHeight = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun CourseCell(item: WeekScheduleItem) {
  val courseColor = Color(item.course.colorArgb)
  val backgroundColor =
    if (item.isActive) courseColor.copy(alpha = 0.20f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
  val textColor =
    if (item.isActive) courseColor
    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
  val status = if (item.isActive) "本周上课" else "本周不上课"

  Column(
    verticalArrangement = Arrangement.spacedBy(2.dp),
    modifier =
      Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(8.dp))
        .background(backgroundColor)
        .semantics {
          stateDescription = status
          contentDescription =
            "${item.course.name}，${item.meeting.classroom}，" +
              "${item.meeting.startTime.timeText()}到${item.meeting.endTime.timeText()}，$status"
        }
        .padding(horizontal = 5.dp, vertical = 7.dp),
  ) {
    Text(
      text = item.course.name,
      color = textColor,
      fontSize = 12.sp,
      lineHeight = 15.sp,
      fontWeight = FontWeight.SemiBold,
      maxLines = 3,
      overflow = TextOverflow.Ellipsis,
    )
    if (item.meeting.classroom.isNotBlank()) {
      Text(
        text = item.meeting.classroom,
        color = textColor,
        fontSize = 9.sp,
        lineHeight = 11.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    }
    Text(
      text = item.compactWeekLabel(),
      color = textColor,
      fontSize = 9.sp,
      lineHeight = 11.sp,
      maxLines = 2,
    )
  }
}

@Composable
private fun BreakRow(label: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier =
      Modifier
        .fillMaxWidth()
        .height(28.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant),
  ) {
    Spacer(Modifier.width(timeColumnWidth))
    Text(
      text = label,
      textAlign = TextAlign.Center,
      fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.weight(1f),
    )
  }
}

private fun WeekScheduleItem.compactWeekLabel(): String {
  val parity =
    when (meeting.weekPattern.parity) {
      WeekParity.EVERY_WEEK -> "每周"
      WeekParity.ODD_WEEKS -> "单周"
      WeekParity.EVEN_WEEKS -> "双周"
    }
  return "${meeting.weekPattern.startWeek}–${meeting.weekPattern.endWeek}周 $parity"
}

private fun DayOfWeek.chineseName(): String =
  when (this) {
    DayOfWeek.MONDAY -> "周一"
    DayOfWeek.TUESDAY -> "周二"
    DayOfWeek.WEDNESDAY -> "周三"
    DayOfWeek.THURSDAY -> "周四"
    DayOfWeek.FRIDAY -> "周五"
    DayOfWeek.SATURDAY -> "周六"
    DayOfWeek.SUNDAY -> "周日"
  }

private val shortDateFormatter = DateTimeFormatter.ofPattern("M月d日")
private val monthDayFormatter = DateTimeFormatter.ofPattern("M/d")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun LocalDate.shortDate(): String = format(shortDateFormatter)

private fun LocalDate.monthDay(): String = format(monthDayFormatter)

private fun LocalTime.timeText(): String = format(timeFormatter)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MainScreenPreview() {
  val week = 2
  val term = SampleTimetableData.term
  TimetableAppTheme {
    MainScreen(
      state =
        MainScreenUiState(
          termName = term.name,
          selectedWeek = week,
          totalWeeks = term.totalWeeks,
          weekStart = term.dateOf(week, DayOfWeek.MONDAY),
          weekEnd = term.dateOf(week, DayOfWeek.SUNDAY),
          periods = SampleTimetableData.periods,
          items = BuildWeekSchedule()(week, SampleTimetableData.courses, SampleTimetableData.meetings),
        ),
      onPreviousWeek = {},
      onNextWeek = {},
    )
  }
}
