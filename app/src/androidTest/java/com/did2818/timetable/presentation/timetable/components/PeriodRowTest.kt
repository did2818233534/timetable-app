package com.did2818.timetable.presentation.timetable.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.SplitDisplaySlot
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PeriodRowTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun twoCourses_shareOneSlotVertically() {
    val first = item("first", "第一门课程")
    val second = item("second", "第二门课程")
    var openedCount = 0
    composeTestRule.setContent {
      MaterialTheme {
        PeriodRow(
          period = period,
          items = listOf(first, second),
          visibleDays = listOf(DayOfWeek.MONDAY),
          selectedWeek = 3,
          totalWeeks = 18,
          splitDisplaySlots = setOf(SplitDisplaySlot(DayOfWeek.MONDAY, 1)),
          onOpenCell = { _, _, items -> openedCount = items.size },
          onCopyItem = {},
          onPasteCell = { _, _ -> },
        )
      }
    }

    composeTestRule.onNodeWithText("第一门课程").assertExists()
    composeTestRule.onNodeWithText("第二门课程").assertExists()
    val slot = composeTestRule.onNodeWithTag("slot-MONDAY-1").fetchSemanticsNode().boundsInRoot
    val firstBounds = courseBounds("first-meeting")
    val secondBounds = courseBounds("second-meeting")
    assertTrue(firstBounds.bottom <= secondBounds.top)
    assertTrue(firstBounds.height in slot.height * 0.35f..slot.height * 0.55f)
    assertTrue(secondBounds.height in slot.height * 0.35f..slot.height * 0.55f)

    composeTestRule.onNodeWithTag("slot-MONDAY-1").performClick()
    composeTestRule.runOnIdle { assertEquals(2, openedCount) }
  }

  @Test
  fun slotWithoutSplitConfiguration_displaysOnlyHighestPriorityCourse() {
    composeTestRule.setContent {
      MaterialTheme {
        PeriodRow(
          period = period,
          items = listOf(item("first", "第一门课程"), item("second", "第二门课程")),
          visibleDays = listOf(DayOfWeek.MONDAY),
          selectedWeek = 3,
          totalWeeks = 18,
          splitDisplaySlots = emptySet(),
          onOpenCell = { _, _, _ -> },
          onCopyItem = {},
          onPasteCell = { _, _ -> },
        )
      }
    }

    composeTestRule.onNodeWithText("第一门课程").assertExists()
    composeTestRule.onNodeWithText("第二门课程").assertDoesNotExist()
  }

  private fun courseBounds(meetingId: String) =
    composeTestRule
      .onNodeWithTag("slot-course-$meetingId", useUnmergedTree = true)
      .fetchSemanticsNode()
      .boundsInRoot

  private fun item(id: String, name: String): WeekScheduleItem =
    WeekScheduleItem(
      course = Course(id, name),
      meeting =
        ClassMeeting(
          id = "$id-meeting",
          courseId = id,
          dayOfWeek = DayOfWeek.MONDAY,
          startTime = period.startTime,
          endTime = period.endTime,
          weekPattern = WeekPattern(1, 18),
        ),
      isActive = true,
    )

  private companion object {
    val period = ClassPeriod(1, LocalTime.of(8, 0), LocalTime.of(9, 35))
  }
}
