package com.did2818.timetable.presentation.timetable.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.did2818.timetable.domain.model.TimetableSummary
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TimetableActionsMenuTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun hamburgerContainsActionsAndCanSwitchTimetable() {
    var selected = ""
    composeTestRule.setContent {
      MaterialTheme {
        TimetableActionsMenu(
          timetables =
            listOf(
              TimetableSummary("first", "第一份课表"),
              TimetableSummary("second", "第二份课表"),
            ),
          selectedTimetableId = "first",
          onImportClick = {},
          onExportClick = {},
          onNewClick = {},
          onSettingsClick = {},
          onTimetableSelected = { selected = it },
          exportEnabled = true,
          settingsEnabled = true,
        )
      }
    }

    composeTestRule.onNodeWithText("导入课程表").assertDoesNotExist()
    composeTestRule.onNodeWithTag("timetable-menu-button").performClick()
    listOf("切换课程表", "导入课程表", "导出课程表", "新建课程表", "设置")
      .forEach { composeTestRule.onNodeWithText(it).assertExists() }

    composeTestRule.onNodeWithText("切换课程表").performClick()
    composeTestRule.onNodeWithText("✓ 第一份课表").assertExists()
    composeTestRule.onNodeWithText("第二份课表").performClick()
    composeTestRule.runOnIdle { assertEquals("second", selected) }
  }
}
