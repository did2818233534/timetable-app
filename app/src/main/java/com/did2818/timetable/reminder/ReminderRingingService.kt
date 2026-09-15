package com.did2818.timetable.reminder

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder

class ReminderRingingService : Service() {
  private var player: MediaPlayer? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val payload = intent?.reminderPayload() ?: run {
      stopSelf(startId)
      return START_NOT_STICKY
    }
    startForeground(payload.notificationId, ReminderNotificationFactory(this).build(payload))
    startRinging()
    return START_NOT_STICKY
  }

  private fun startRinging() {
    player?.release()
    val sound =
      RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ?: return
    val candidate = MediaPlayer()
    player =
      runCatching { configureAndStart(candidate, sound) }
        .onFailure { candidate.release() }
        .getOrNull()
  }

  private fun configureAndStart(player: MediaPlayer, sound: android.net.Uri): MediaPlayer =
    player.apply {
      setDataSource(this@ReminderRingingService, sound)
      setAudioAttributes(
        AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_ALARM)
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .build(),
      )
      isLooping = true
      prepare()
      start()
    }

  override fun onDestroy() {
    player?.run {
      runCatching { stop() }
      release()
    }
    player = null
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
