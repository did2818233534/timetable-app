package com.did2818.timetable.presentation.timetable

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.did2818.timetable.data.sample.SampleTimetableData
import com.did2818.timetable.domain.usecase.BuildWeekSchedule
import java.time.DayOfWeek
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [MainScreen]. */
class MainScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    val week = 2
    val term = SampleTimetableData.term
    composeTestRule.setContent {
      MainScreen(
        state =
          MainScreenUiState(
            termName = term.name,
            selectedWeek = week,
            totalWeeks = term.totalWeeks,
            weekStart = term.dateOf(week, DayOfWeek.MONDAY),
            weekEnd = term.dateOf(week, DayOfWeek.SUNDAY),
            periods = SampleTimetableData.periods,
            items = BuildWeekSchedule()(week, SampleTimetableData.courses, SampleTimetableData.meetings),
          ),
        onPreviousWeek = {},
        onNextWeek = {},
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
}
