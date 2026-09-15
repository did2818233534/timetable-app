package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.presentation.common.format.timeText
import com.did2818.timetable.presentation.timetable.compactWeekLabel

@Composable
internal fun CourseCell(item: WeekScheduleItem, hasConflict: Boolean) {
  val style = courseCellStyle(item)
  val status = if (item.isActive) "本周上课" else "本周不上课"
  val state = if (hasConflict) "$status，有时间冲突" else status
  Box(
    modifier =
      Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(8.dp))
        .background(style.background)
        .semantics(mergeDescendants = true) {
          stateDescription = state
          contentDescription = item.accessibilityText(state)
        },
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(2.dp),
      modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 7.dp),
    ) {
      CellText(item.course.name, style.text, 12, FontWeight.SemiBold, 3)
      item.displayNote().takeIf(String::isNotBlank)?.let { note ->
        CellText(note, style.text, 9, FontWeight.Normal, 2)
      }
      CellText(item.compactWeekLabel(), style.text, 9, FontWeight.Normal, 2)
    }
    if (hasConflict) {
      Text(
        text = "!",
        color = MaterialTheme.colorScheme.onError,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        modifier =
          Modifier
            .align(Alignment.TopEnd)
            .size(15.dp)
            .background(MaterialTheme.colorScheme.error, CircleShape)
            .padding(horizontal = 5.dp),
      )
    }
  }
}

@Composable
private fun CellText(
  text: String,
  color: Color,
  sizeSp: Int,
  weight: FontWeight,
  maxLines: Int,
) {
  Text(
    text = text,
    color = color,
    fontSize = sizeSp.sp,
    lineHeight = (sizeSp + 3).sp,
    fontWeight = weight,
    maxLines = maxLines,
    overflow = TextOverflow.Ellipsis,
  )
}

@Composable
private fun courseCellStyle(item: WeekScheduleItem): CourseCellStyle {
  if (!item.isActive) {
    return CourseCellStyle(
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
      MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
    )
  }
  val courseColor = Color(item.course.colorArgb)
  return CourseCellStyle(courseColor.copy(alpha = 0.20f), courseColor)
}

private fun WeekScheduleItem.accessibilityText(status: String): String =
  "${course.name}，${meeting.startTime.timeText()}到${meeting.endTime.timeText()}，$status" +
    displayNote().takeIf(String::isNotBlank)?.let { "，备注：$it" }.orEmpty()

private fun WeekScheduleItem.displayNote(): String = meeting.displayNote.ifBlank { course.note }

private data class CourseCellStyle(val background: Color, val text: Color)
