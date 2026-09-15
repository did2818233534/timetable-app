package com.did2818.timetable.presentation.timetable.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.model.ReminderSettings
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ReminderSettingsDialogTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun globalReminder_canSelectScopeMinutesAndAlarmDelivery() {
    var saved: ReminderSettings? = null
    composeTestRule.setContent {
      ReminderSettingsDialog(ReminderSettings(), onDismiss = {}, onSave = { saved = it })
    }

    composeTestRule.onNodeWithText("每节课都提醒").performClick()
    composeTestRule.onNodeWithText("10").performTextReplacement("25")
    composeTestRule.onNodeWithText("闹钟提醒").performClick()
    composeTestRule.onNodeWithText("保存").performClick()

    composeTestRule.runOnIdle {
      assertEquals(ReminderSettings(ReminderScope.ALL_CLASSES, 25, ReminderDelivery.ALARM), saved)
    }
  }
}
