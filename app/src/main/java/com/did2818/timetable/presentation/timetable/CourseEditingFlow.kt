package com.did2818.timetable.presentation.timetable

import androidx.compose.runtime.Composable
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.usecase.ClassEdit
import com.did2818.timetable.presentation.timetable.components.CourseEditorDialog
import com.did2818.timetable.presentation.timetable.components.SlotCoursesDialog
import java.time.DayOfWeek

@Composable
internal fun CourseEditingFlow(
  state: MainScreenUiState,
  editorTarget: CourseEditorTarget?,
  slotSelection: SlotSelection?,
  onEditorTargetChange: (CourseEditorTarget?) -> Unit,
  onSlotSelectionChange: (SlotSelection?) -> Unit,
  onAdd: (DayOfWeek, ClassPeriod, ClassEdit) -> Unit,
  onUpdate: (WeekScheduleItem, ClassEdit) -> Unit,
  onDelete: (WeekScheduleItem) -> Unit,
  onReminderPermissionRequest: () -> Unit,
) {
  editorTarget?.let { target ->
    val existing = (target as? CourseEditorTarget.Existing)?.item
    CourseEditorDialog(
      title = "${target.day.displayName()} 第 ${target.period.number} 节",
      initial = existing?.toEdit() ?: newClassEdit(state),
      totalWeeks = state.totalWeeks,
      onDismiss = { onEditorTargetChange(null) },
      onSave = { edit ->
        if (existing == null) onAdd(target.day, target.period, edit) else onUpdate(existing, edit)
        if (edit.reminderOverride?.enabled == true) onReminderPermissionRequest()
        onEditorTargetChange(null)
      },
      onDelete = existing?.let { item ->
        {
          onDelete(item)
          onEditorTargetChange(null)
        }
      },
    )
  }
  slotSelection?.let { slot ->
    SlotCoursesDialog(
      title = "${slot.day.displayName()} 第 ${slot.period.number} 节的课程",
      items = slot.items,
      onSelect = { item ->
        onSlotSelectionChange(null)
        onEditorTargetChange(CourseEditorTarget.Existing(item, slot.period))
      },
      onAdd = {
        onSlotSelectionChange(null)
        onEditorTargetChange(CourseEditorTarget.New(slot.day, slot.period))
      },
      onDismiss = { onSlotSelectionChange(null) },
    )
  }
}

private fun WeekScheduleItem.toEdit() =
  ClassEdit(
    course.name,
    meeting.displayNote.ifBlank { course.note },
    meeting.weekPattern,
    meeting.reminderOverride,
  )

private fun newClassEdit(state: MainScreenUiState) =
  ClassEdit("", "", WeekPattern(state.selectedWeek, state.totalWeeks, WeekParity.EVERY_WEEK))

private fun DayOfWeek.displayName(): String =
  listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")[value - 1]
