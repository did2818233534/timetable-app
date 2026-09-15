package com.did2818.timetable.reminder

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.did2818.timetable.presentation.theme.TimetableAppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReminderAlarmActivity : ComponentActivity() {
  private var payload: ReminderPayload? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      setShowWhenLocked(true)
      setTurnScreenOn(true)
    } else {
      window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
          WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
      )
    }
    payload = intent.reminderPayload()
    val reminder = payload ?: run { finish(); return }
    setContent {
      TimetableAppTheme {
        BackHandler { dismissAlarm() }
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize().padding(32.dp),
        ) {
          Text("上课提醒", style = MaterialTheme.typography.headlineLarge)
          Text(reminder.courseName, style = MaterialTheme.typography.headlineMedium)
          Text("${reminder.classTimeText()} 上课")
          if (reminder.note.isNotBlank()) Text(reminder.note)
          Button(onClick = ::dismissAlarm, modifier = Modifier.padding(top = 32.dp)) {
            Text("关闭闹钟")
          }
        }
      }
    }
  }

  private fun dismissAlarm() {
    payload?.let {
      getSystemService(NotificationManager::class.java).cancel(it.notificationId)
    }
    stopService(Intent(this, ReminderRingingService::class.java))
    finishAndRemoveTask()
  }
}

private fun ReminderPayload.classTimeText(): String =
  Instant.ofEpochMilli(classStartsAtMillis)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("HH:mm"))
