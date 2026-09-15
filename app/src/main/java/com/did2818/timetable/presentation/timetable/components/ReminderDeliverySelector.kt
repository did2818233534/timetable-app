package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.ReminderDelivery

@Composable
internal fun ReminderDeliverySelector(
  selected: ReminderDelivery,
  onSelect: (ReminderDelivery) -> Unit,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    ReminderDelivery.entries.forEach { delivery ->
      FilterChip(
        selected = selected == delivery,
        onClick = { onSelect(delivery) },
        label = { Text(if (delivery == ReminderDelivery.POPUP) "弹窗通知" else "闹钟提醒") },
      )
    }
  }
}
