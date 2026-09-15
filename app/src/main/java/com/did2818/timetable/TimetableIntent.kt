package com.did2818.timetable

import android.content.Intent
import android.net.Uri
import android.os.Build

internal fun Intent.timetableDocumentUri(): Uri? =
  when (action) {
    Intent.ACTION_VIEW -> data
    Intent.ACTION_SEND -> streamUri()
    else -> null
  }

private fun Intent.streamUri(): Uri? =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
  } else {
    @Suppress("DEPRECATION")
    getParcelableExtra(Intent.EXTRA_STREAM)
  }
