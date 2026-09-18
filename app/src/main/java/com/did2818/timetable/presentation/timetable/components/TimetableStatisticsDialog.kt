package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.TimetableStatistics
import java.time.DayOfWeek

@Composable
fun TimetableStatisticsDialog(
  statistics: TimetableStatistics,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("课程统计") },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
      ) {
        StatisticRow("本周还剩", "${statistics.remainingThisWeek} 节")
        StatisticRow("距离最后一节课", statistics.lastClassText())
        StatisticRow("距离上完所有课还剩", "${statistics.remainingClasses} 节")
        StatisticRow("已经上了", "${statistics.completedClasses} 节")
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        Text("本周每天还剩", style = MaterialTheme.typography.titleSmall)
        DayOfWeek.entries.forEach { day ->
          StatisticRow(day.chineseName(), "${statistics.remainingByDay.getValue(day)} 节")
        }
      }
    },
    confirmButton = { TextButton(onClick = onDismiss) { Text("知道了") } },
  )
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
