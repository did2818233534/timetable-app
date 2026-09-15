package com.did2818.timetable.presentation.timetable

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
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
  private var previousCount = 0
  private var nextCount = 0
  private var copyCount = 0
  private var pasteCount = 0
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
        onPreviousWeek = { previousCount++ },
        onNextWeek = { nextCount++ },
        onImportClick = {},
        onOpenCell = { _, _, items -> openedCellSizes += items.size },
        onCopyItem = { copyCount++ },
        onPasteCell = { _, _ -> pasteCount++ },
      )
    }
  }

  @Test
  fun schedule_displaysWeekAndCourses() {
    composeTestRule.onNodeWithText("第 2 周").assertExists()
    composeTestRule.onNodeWithText("周日").assertExists()
    composeTestRule.onNodeWithText("高等数学").assertExists()
    composeTestRule.onNodeWithText("大学物理").assertExists()
  }

  @Test
  fun inactiveOddWeekCourse_isMarkedAsNotTakingPlace() {
    composeTestRule.onNode(hasStateDescription("本周不上课")).assertExists()
  }

  @Test
  fun importEntry_isVisibleAndClickable() {
    composeTestRule.onNodeWithText("导入课表").assertHasClickAction()
  }

  @Test
  fun horizontalSwipes_requestAdjacentWeeks() {
    composeTestRule.onRoot().performTouchInput { swipeLeft() }
    composeTestRule.onRoot().performTouchInput { swipeRight() }

    composeTestRule.runOnIdle {
      assertEquals(1, nextCount)
      assertEquals(1, previousCount)
    }
  }

  @Test
  fun cells_supportEditCreateCopyAndPasteGestures() {
    composeTestRule.onNodeWithTag("slot-MONDAY-1").performClick()
    composeTestRule.onNodeWithTag("slot-MONDAY-1").performTouchInput { longClick() }
    composeTestRule.onNodeWithTag("slot-SATURDAY-5").performClick()
    composeTestRule.onNodeWithTag("slot-SATURDAY-5").performTouchInput { longClick() }

    composeTestRule.runOnIdle {
      assertEquals(listOf(1, 0), openedCellSizes)
      assertEquals(1, copyCount)
      assertEquals(1, pasteCount)
    }
  }
}
