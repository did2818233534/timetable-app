package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.TimetableStatistics
import java.time.DayOfWeek

@Composable
fun TimetableStatisticsDialog(
  statistics: TimetableStatistics,
  onDismiss: () -> Unit,
) {
  var showDailyStatistics by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("课程统计") },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
      ) {
        ExpandableWeeklyStatistic(
          statistics = statistics,
          expanded = showDailyStatistics,
          onExpandedChange = { showDailyStatistics = !showDailyStatistics },
        )
        StatisticRow("距离最后一节课", statistics.lastClassText())
        StatisticRow("距离上完所有课还剩", "${statistics.remainingClasses} 节")
        StatisticRow("已经上了", "${statistics.completedClasses} 节")
      }
    },
    confirmButton = { TextButton(onClick = onDismiss) { Text("知道了") } },
  )
}

@Composable
private fun ExpandableWeeklyStatistic(
  statistics: TimetableStatistics,
  expanded: Boolean,
  onExpandedChange: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
        Modifier.fillMaxWidth().clickable(
          role = Role.Button,
          onClickLabel = if (expanded) "收起每天统计" else "展开每天统计",
          onClick = onExpandedChange,
        ),
    ) {
      Text("本周还剩")
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("${statistics.remainingThisWeek} 节", fontWeight = FontWeight.SemiBold)
        ExpandChevron(expanded)
      }
    }
    if (expanded) {
      HorizontalDivider(Modifier.padding(vertical = 2.dp))
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 16.dp),
      ) {
        DayOfWeek.entries.forEach { day ->
          StatisticRow(day.chineseName(), "${statistics.remainingByDay.getValue(day)} 节")
        }
      }
    }
  }
}

@Composable
private fun ExpandChevron(expanded: Boolean) {
  val color = MaterialTheme.colorScheme.onSurfaceVariant
  Box(Modifier.size(20.dp)) {
    Canvas(Modifier.size(20.dp)) {
      val left = Offset(size.width * 0.25f, size.height * if (expanded) 0.65f else 0.4f)
      val center = Offset(size.width * 0.5f, size.height * if (expanded) 0.4f else 0.65f)
      val right = Offset(size.width * 0.75f, size.height * if (expanded) 0.65f else 0.4f)
      drawLine(color, left, center, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
      drawLine(color, center, right, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
    }
  }
}

@Composable
private fun StatisticRow(label: String, value: String) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(label)
    Text(value, fontWeight = FontWeight.SemiBold)
  }
}

private fun TimetableStatistics.lastClassText(): String =
  when {
    totalClasses == 0 -> "暂无课程"
    lastClassAt == null -> "全部课程已结束"
    weeksUntilLastClass == 0 -> "本周内"
    else -> "$weeksUntilLastClass 周"
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
