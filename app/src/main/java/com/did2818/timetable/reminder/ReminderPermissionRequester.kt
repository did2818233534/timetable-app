package com.did2818.timetable.reminder

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri

class ReminderPermissionRequester(private val activity: ComponentActivity) {
  private val notificationPermission =
    activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      if (granted) requestExactAlarmAccess()
    }

  fun request() {
    if (
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
          PackageManager.PERMISSION_GRANTED
    ) {
      notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    } else requestExactAlarmAccess()
  }

  private fun requestExactAlarmAccess() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val manager = activity.getSystemService(AlarmManager::class.java)
    if (manager.canScheduleExactAlarms()) return
    runCatching {
      activity.startActivity(
        Intent(
          Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
          "package:${activity.packageName}".toUri(),
        ),
      )
    }
  }
}
