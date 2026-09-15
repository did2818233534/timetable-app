package com.did2818.timetable.presentation.common.file

import java.io.ByteArrayOutputStream
import java.io.InputStream

fun InputStream.readUtf8Text(maxBytes: Int = MAX_DOCUMENT_BYTES): String {
  require(maxBytes > 0)
  val output = ByteArrayOutputStream()
  val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
  var totalBytes = 0
  while (true) {
    val count = read(buffer)
    if (count < 0) break
    totalBytes += count
    require(totalBytes <= maxBytes) { "课表文件不能超过 ${maxBytes / 1024} KB" }
    output.write(buffer, 0, count)
  }
  return output.toString(Charsets.UTF_8.name())
}

const val MAX_DOCUMENT_BYTES = 1024 * 1024
