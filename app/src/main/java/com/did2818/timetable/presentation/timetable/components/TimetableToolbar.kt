package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.TimetableSummary

@Composable
fun TimetableToolbar(
  termName: String,
  onImportClick: () -> Unit,
  onExportClick: () -> Unit,
  onNewClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onStatisticsClick: () -> Unit,
  timetables: List<TimetableSummary> = emptyList(),
  selectedTimetableId: String? = null,
  onTimetableSelected: (String) -> Unit = {},
  exportEnabled: Boolean = true,
  settingsEnabled: Boolean = true,
  statisticsEnabled: Boolean = true,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth().height(48.dp).padding(start = 12.dp, end = 4.dp),
  ) {
    Text(
      text = termName,
      style = MaterialTheme.typography.titleSmall,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f),
    )
    TimetableActionsMenu(
      timetables = timetables,
      selectedTimetableId = selectedTimetableId,
      onImportClick = onImportClick,
      onExportClick = onExportClick,
      onNewClick = onNewClick,
      onSettingsClick = onSettingsClick,
      onStatisticsClick = onStatisticsClick,
      onTimetableSelected = onTimetableSelected,
      exportEnabled = exportEnabled,
      settingsEnabled = settingsEnabled,
      statisticsEnabled = statisticsEnabled,
    )
  }
}
