package com.did2818.timetable.presentation.timetable.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SlotCoursesDialogTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun splitDisplaySwitch_reportsUserChoice() {
    var enabled = false
    composeTestRule.setContent {
      MaterialTheme {
        SlotCoursesDialog(
          title = "周一 第 1 节的课程",
          items = listOf(item("first"), item("second")),
          splitDisplay = false,
          onSplitDisplayChange = { enabled = it },
          onSelect = {},
          onAdd = {},
          onDismiss = {},
        )
      }
    }

    composeTestRule.onNodeWithText("上下分栏显示两门课程").assertExists()
    composeTestRule.onNodeWithText("关闭时只显示当前或下一门课程").assertExists()
    composeTestRule.onNodeWithTag("split-display-switch").assertIsOff().performClick()
    composeTestRule.runOnIdle { assertTrue(enabled) }
  }

  private fun item(id: String): WeekScheduleItem =
    WeekScheduleItem(
      course = Course(id, id),
      meeting =
        ClassMeeting(
          id = "$id-meeting",
          courseId = id,
          dayOfWeek = DayOfWeek.MONDAY,
          startTime = LocalTime.of(8, 0),
          endTime = LocalTime.of(9, 35),
          weekPattern = WeekPattern(1, 18),
        ),
      isActive = true,
    )
}
