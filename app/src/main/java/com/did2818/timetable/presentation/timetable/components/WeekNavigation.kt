package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.did2818.timetable.presentation.common.format.shortDateText
import com.did2818.timetable.presentation.timetable.MainScreenUiState

@Composable
fun WeekNavigation(
  state: MainScreenUiState,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth().height(76.dp),
  ) {
    WeekArrow("‹", state.selectedWeek > 1, onPreviousWeek)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "第 ${state.selectedWeek} 周",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = "${state.weekStart.shortDateText()} – ${state.weekEnd.shortDateText()}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    WeekArrow("›", state.selectedWeek < state.totalWeeks, onNextWeek)
  }
}

@Composable
private fun WeekArrow(
  label: String,
  enabled: Boolean,
  onClick: () -> Unit,
) {
  TextButton(onClick = onClick, enabled = enabled) {
    Text(label, fontSize = 36.sp, fontWeight = FontWeight.Light)
  }
}
