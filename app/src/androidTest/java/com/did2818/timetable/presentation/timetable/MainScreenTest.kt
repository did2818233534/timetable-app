package com.did2818.timetable.presentation.timetable

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.longClick
import com.did2818.timetable.data.sample.SampleTimetableRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [MainScreen]. */
class MainScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()
  private val selectedWeeks = mutableListOf<Int>()
  private var copyCount = 0
  private var pasteCount = 0
  private var settingsCount = 0
  private val openedCellSizes = mutableListOf<Int>()

  @Before
  fun setup() {
    val state =
      MainScreenStateFactory().create(
        timetable = SampleTimetableRepository().timetable.value,
        requestedWeek = 2,
      )
    composeTestRule.setContent {
      MainScreen(
        state = state,
        stateForWeek = { MainScreenStateFactory().create(SampleTimetableRepository().timetable.value, it) },
        onSelectWeek = { selectedWeeks += it },
        onImportClick = {},
        onExportClick = {},
        onNewClick = {},
        onSettingsClick = { settingsCount++ },
        onOpenCell = { _, _, items -> openedCellSizes += items.size },
        onCopyItem = { copyCount++ },
        onPasteCell = { _, _ -> pasteCount++ },
      )
    }
  }

  @Test
  fun schedule_displaysWeekAndCourses() {
    composeTestRule.onNodeWithText("第 2 周").assertExists()
    composeTestRule.onNode(hasText("周日") and inWeek(2)).assertExists()
    composeTestRule.onNode(hasText("高等数学") and inWeek(2)).assertExists()
    composeTestRule.onNode(hasText("大学物理") and inWeek(2)).assertExists()
  }

  @Test
  fun inactiveOddWeekCourse_isMarkedAsNotTakingPlace() {
    composeTestRule.onNode(hasStateDescription("本周不上课") and inWeek(2)).assertExists()
  }

  @Test
  fun timetableActions_areVisibleAndClickable() {
    composeTestRule.onNodeWithText("导入").assertHasClickAction()
    composeTestRule.onNodeWithText("导出").assertHasClickAction()
    composeTestRule.onNodeWithText("新建").assertHasClickAction()
    composeTestRule.onNodeWithText("设置").assertHasClickAction()
    composeTestRule.onNodeWithText("设置").performClick()
    composeTestRule.runOnIdle { assertEquals(1, settingsCount) }
  }

  @Test
  fun horizontalSwipes_requestAdjacentWeeks() {
    composeTestRule.onNodeWithTag("week-page-2").performTouchInput { swipeLeft() }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("week-page-3").performTouchInput { swipeRight() }

    composeTestRule.runOnIdle {
      assertEquals(listOf(3, 2), selectedWeeks)
    }
  }

  @Test
  fun cells_supportEditCreateCopyAndPasteGestures() {
    val monday = hasTestTag("slot-MONDAY-1") and inWeek(2)
    val saturday = hasTestTag("slot-SATURDAY-5") and inWeek(2)
    composeTestRule.onNode(monday).performClick()
    composeTestRule.onNode(monday).performTouchInput { longClick() }
    composeTestRule.onNode(saturday).performClick()
    composeTestRule.onNode(saturday).performTouchInput { longClick() }

    composeTestRule.runOnIdle {
      assertEquals(listOf(1, 0), openedCellSizes)
      assertEquals(1, copyCount)
      assertEquals(1, pasteCount)
    }
  }

  private fun inWeek(week: Int) = hasAnyAncestor(hasTestTag("week-page-$week"))
}
