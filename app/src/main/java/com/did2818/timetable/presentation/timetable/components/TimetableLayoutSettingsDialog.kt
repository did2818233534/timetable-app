package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.usecase.TimetableLayoutEdit
import com.did2818.timetable.presentation.timetable.TimetableLayoutFormParser
import com.did2818.timetable.presentation.timetable.TimetablePeriodDraft
import java.time.DayOfWeek

@Composable
internal fun TimetableLayoutSettingsDialog(
  periods: List<ClassPeriod>,
  configuredDays: Set<DayOfWeek>,
  initialHideEmptyDays: Boolean,
  onDismiss: () -> Unit,
  onSave: (TimetableLayoutEdit) -> Unit,
) {
  var drafts by remember(periods) { mutableStateOf(periods.map(ClassPeriod::toDraft)) }
  var selectedDays by remember(configuredDays) { mutableStateOf(configuredDays) }
  var hideEmptyDays by remember { mutableStateOf(initialHideEmptyDays) }
  val result = runCatching { TimetableLayoutFormParser()(drafts, selectedDays, hideEmptyDays) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("课表设置") },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
      ) {
        Text("显示星期")
        DayChips(selectedDays) { day ->
          selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = hideEmptyDays, onCheckedChange = { hideEmptyDays = it })
          Text("自动隐藏整学期没有课程的星期")
        }
        Text("每天 ${drafts.size} 节课（勾选表示在表格中显示）")
        drafts.forEachIndexed { index, draft ->
          TimetablePeriodSettingsRow(
            number = index + 1,
            period = draft,
            onChange = { changed -> drafts = drafts.toMutableList().also { it[index] = changed } },
            onDelete = { drafts = drafts.toMutableList().also { it.removeAt(index) } },
          )
        }
        TextButton(onClick = { drafts = drafts + nextPeriod(drafts) }) { Text("+ 添加节次") }
        result.exceptionOrNull()?.message?.let { Text(it) }
      }
    },
    confirmButton = {
      TextButton(enabled = result.isSuccess, onClick = { onSave(result.getOrThrow()) }) {
        Text("保存")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
  )
}

@Composable
private fun DayChips(selectedDays: Set<DayOfWeek>, onToggle: (DayOfWeek) -> Unit) {
  DayOfWeek.entries.chunked(4).forEach { days ->
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      days.forEach { day ->
        FilterChip(
          selected = day in selectedDays,
          onClick = { onToggle(day) },
          label = { Text(DAY_NAMES[day.value - 1]) },
        )
      }
    }
  }
}

private fun ClassPeriod.toDraft() =
  TimetablePeriodDraft(number, startTime.toString(), endTime.toString(), visible)

private fun nextPeriod(periods: List<TimetablePeriodDraft>): TimetablePeriodDraft {
  val start = periods.lastOrNull()?.endTime ?: "08:00"
  return TimetablePeriodDraft(null, start, "23:59", visible = true)
}

private val DAY_NAMES = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
