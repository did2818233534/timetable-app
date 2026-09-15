package com.did2818.timetable.presentation.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.did2818.timetable.data.sample.SampleTimetableData
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.usecase.BuildWeekSchedule
import com.did2818.timetable.presentation.theme.TimetableAppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    Text(
      text = "课程表",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
    )
    Text(
      text = state.termName,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(Modifier.height(12.dp))
    WeekSelector(
      state = state,
      onPreviousWeek = onPreviousWeek,
      onNextWeek = onNextWeek,
    )
    Spacer(Modifier.height(12.dp))

    if (state.items.isEmpty()) {
      EmptySchedule(modifier = Modifier.fillMaxSize())
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
      ) {
        items(state.items, key = { it.meeting.id }) { item -> CourseCard(item) }
      }
    }
  }
}

@Composable
private fun WeekSelector(
  state: MainScreenUiState,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier =
      Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(horizontal = 4.dp, vertical = 6.dp),
  ) {
    TextButton(onClick = onPreviousWeek, enabled = state.selectedWeek > 1) {
      Text("上一周")
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "第 ${state.selectedWeek} 周",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
      )
      Text(
        text = "${state.weekStart.shortDate()} – ${state.weekEnd.shortDate()}",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    TextButton(onClick = onNextWeek, enabled = state.selectedWeek < state.totalWeeks) {
      Text("下一周")
    }
  }
}

@Composable
private fun CourseCard(item: WeekScheduleItem) {
  val contentAlpha = if (item.isActive) 1f else 0.38f
  val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
  val status = if (item.isActive) "本周上课" else "本周不上课"

  Card(
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = contentColor,
      ),
    modifier =
      Modifier
        .fillMaxWidth()
        .semantics { stateDescription = status },
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.Top,
      modifier = Modifier.padding(14.dp),
    ) {
      Box(
        modifier =
          Modifier
            .padding(top = 4.dp)
            .size(width = 5.dp, height = 58.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(item.course.colorArgb).copy(alpha = contentAlpha)),
      )
      Column(modifier = Modifier.weight(1f)) {
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
            text = item.course.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
          )
          if (!item.isActive) {
            Text(
              text = "本周不上",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
            )
          }
        }
        Spacer(Modifier.height(3.dp))
        Text(
          text =
            "${item.meeting.dayOfWeek.chineseName()}  " +
              "${item.meeting.startTime.timeText()}–${item.meeting.endTime.timeText()}",
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text =
            listOf(item.meeting.classroom, item.course.teacher)
              .filter(String::isNotBlank)
              .joinToString(" · "),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
        )
        Text(
          text = item.weekLabel(),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
        )
      }
    }
  }
}

@Composable
private fun EmptySchedule(modifier: Modifier = Modifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Text("本周还没有课程", color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

private fun WeekScheduleItem.weekLabel(): String {
  val range = "第 ${meeting.weekPattern.startWeek}–${meeting.weekPattern.endWeek} 周"
  val parity =
    when (meeting.weekPattern.parity) {
      WeekParity.EVERY_WEEK -> "每周"
      WeekParity.ODD_WEEKS -> "单周"
      WeekParity.EVEN_WEEKS -> "双周"
    }
  return "$range · $parity"
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
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun LocalDate.shortDate(): String = format(shortDateFormatter)

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
          items = BuildWeekSchedule()(week, SampleTimetableData.courses, SampleTimetableData.meetings),
        ),
      onPreviousWeek = {},
      onNextWeek = {},
      modifier = Modifier.padding(16.dp),
    )
  }
}
