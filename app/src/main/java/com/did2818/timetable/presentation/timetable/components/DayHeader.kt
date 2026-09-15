package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.did2818.timetable.presentation.common.format.chineseText
import com.did2818.timetable.presentation.common.format.monthDayText
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun DayHeader(weekStart: LocalDate, visibleDays: List<DayOfWeek>) {
  val gridColor = MaterialTheme.colorScheme.outlineVariant
  Row(modifier = Modifier.fillMaxWidth().height(58.dp)) {
    Box(
      modifier = Modifier.width(TimeColumnWidth).fillMaxHeight().border(GridLineWidth, gridColor),
    )
    visibleDays.forEach { day ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f).fillMaxHeight().border(GridLineWidth, gridColor),
      ) {
        val date = weekStart.plusDays((day.value - 1).toLong())
        Text(day.chineseText(), fontSize = 12.sp)
        Text(
          date.monthDayText(),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 10.sp,
        )
      }
    }
  }
}
