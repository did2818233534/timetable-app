package com.did2818.timetable.domain.model

enum class ReminderScope {
  DISABLED,
  ALL_CLASSES,
  FIRST_CLASS_OF_DAY,
}

enum class ReminderDelivery {
  POPUP,
  ALARM,
}

data class ReminderSettings(
  val scope: ReminderScope = ReminderScope.DISABLED,
  val minutesBefore: Int = 10,
  val delivery: ReminderDelivery = ReminderDelivery.POPUP,
) {
  init {
    require(minutesBefore in 0..MAX_REMINDER_MINUTES) { "提醒时间必须在 0 到 180 分钟之间" }
  }
}

data class ReminderOverride(
  val enabled: Boolean,
  val minutesBefore: Int = 10,
  val delivery: ReminderDelivery = ReminderDelivery.POPUP,
) {
  init {
    require(minutesBefore in 0..MAX_REMINDER_MINUTES) { "提醒时间必须在 0 到 180 分钟之间" }
  }
}

const val MAX_REMINDER_MINUTES = 180
