package com.did2818.timetable.presentation.timetable

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

internal fun Modifier.weekSwipeGesture(
  onSwipeLeft: () -> Unit,
  onSwipeRight: () -> Unit,
): Modifier = pointerInput(onSwipeLeft, onSwipeRight) {
  var distance = 0f
  val threshold = 64.dp.toPx()
  detectHorizontalDragGestures(
    onDragStart = { distance = 0f },
    onHorizontalDrag = { _, amount -> distance += amount },
    onDragCancel = { distance = 0f },
    onDragEnd = {
      if (abs(distance) >= threshold) {
        if (distance < 0) onSwipeLeft() else onSwipeRight()
      }
      distance = 0f
    },
  )
}
