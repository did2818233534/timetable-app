package com.did2818.timetable.presentation.timetable

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.did2818.timetable.data.defaults.emptyTimetable
import org.junit.Rule
import org.junit.Test

class EmptyTimetableScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun unconfiguredApp_showsOnlyCreationAndImportActions() {
    val state = MainScreenStateFactory().create(emptyTimetable(), 1)
    composeTestRule.setContent {
      MainScreen(
        state = state,
        stateForWeek = { state },
        onSelectWeek = {},
        onImportClick = {},
        onExportClick = {},
        onNewClick = {},
        onOpenCell = { _, _, _ -> },
        onCopyItem = {},
        onPasteCell = { _, _ -> },
      )
    }

    composeTestRule.onNodeWithTag("new-timetable").assertExists()
    composeTestRule.onNodeWithText("新建课程表").assertExists()
    composeTestRule.onNodeWithText("第 1 周").assertDoesNotExist()
  }
}
