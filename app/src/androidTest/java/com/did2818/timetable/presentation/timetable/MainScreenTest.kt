package com.did2818.timetable.presentation.timetable

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertHasClickAction
import com.did2818.timetable.data.sample.SampleTimetableRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [MainScreen]. */
class MainScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

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
        onPreviousWeek = {},
        onNextWeek = {},
        onImportClick = {},
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
}
