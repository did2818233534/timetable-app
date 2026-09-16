package com.did2818.timetable.presentation.timetable.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ExportFormatDialogTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun formatButtons_reportSelectedExportType() {
    var selected = ""
    composeTestRule.setContent {
      MaterialTheme {
        ExportFormatDialog(
          onDismiss = {},
          onExportExcel = { selected = "excel" },
          onExportJson = { selected = "json" },
        )
      }
    }

    composeTestRule.onNodeWithText("Excel 课表（.xlsx）").performClick()
    composeTestRule.runOnIdle { assertEquals("excel", selected) }

    composeTestRule.onNodeWithText("应用课表文件（.timetable.json）").performClick()
    composeTestRule.runOnIdle { assertEquals("json", selected) }
  }
}
