package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
internal fun CourseCell(item: WeekScheduleItem) {
  val style = courseCellStyle(item)
  val status = if (item.isActive) "本周上课" else "本周不上课"
  Column(
    verticalArrangement = Arrangement.spacedBy(2.dp),
    modifier =
      Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(8.dp))
        .background(style.background)
        .semantics {
          stateDescription = status
          contentDescription = item.accessibilityText(status)
        }
        .padding(horizontal = 5.dp, vertical = 7.dp),
  ) {
    CellText(item.course.name, style.text, 12, FontWeight.SemiBold, 3)
    if (item.meeting.classroom.isNotBlank()) {
      CellText(item.meeting.classroom, style.text, 9, FontWeight.Normal, 2)
    }
    CellText(item.compactWeekLabel(), style.text, 9, FontWeight.Normal, 2)
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
  "${course.name}，${meeting.classroom}，" +
    "${meeting.startTime.timeText()}到${meeting.endTime.timeText()}，$status"

private data class CourseCellStyle(val background: Color, val text: Color)
