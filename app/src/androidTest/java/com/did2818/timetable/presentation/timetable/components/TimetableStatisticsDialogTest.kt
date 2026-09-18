package com.did2818.timetable.presentation.timetable.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.did2818.timetable.domain.model.TimetableStatistics
import java.time.DayOfWeek
import java.time.LocalDateTime
import org.junit.Rule
import org.junit.Test

class TimetableStatisticsDialogTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun dialogDisplaysSummaryAndEveryWeekday() {
    val daily = DayOfWeek.entries.associateWith { if (it == DayOfWeek.FRIDAY) 2 else 0 }
    composeTestRule.setContent {
      MaterialTheme {
        TimetableStatisticsDialog(
          statistics =
            TimetableStatistics(
              remainingThisWeek = 2,
              weeksUntilLastClass = 4,
              remainingClasses = 12,
              completedClasses = 8,
              remainingByDay = daily,
              lastClassAt = LocalDateTime.of(2026, 10, 16, 17, 25),
            ),
          onDismiss = {},
        )
      }
    }

    listOf("课程统计", "本周还剩", "距离最后一节课", "距离上完所有课还剩", "已经上了")
      .forEach { composeTestRule.onNodeWithText(it).assertExists() }
    DayOfWeek.entries.map(DayOfWeek::testName)
      .forEach { composeTestRule.onNodeWithText(it).assertExists() }
  }
}

private fun DayOfWeek.testName(): String =
  listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")[value - 1]
