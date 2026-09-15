package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.did2818.timetable.domain.usecase.NewTimetableSpec
import com.did2818.timetable.presentation.timetable.NewTimetableFormParser
import java.time.LocalDate

@Composable
fun NewTimetableDialog(
  defaultStartDate: LocalDate,
  onCreate: (NewTimetableSpec) -> Unit,
  onDismiss: () -> Unit,
) {
  var name by remember { mutableStateOf("我的课程表") }
  var startDate by remember { mutableStateOf(defaultStartDate.toString()) }
  var totalWeeks by remember { mutableStateOf("18") }
  var periods by remember { mutableStateOf(DEFAULT_PERIODS) }
  var error by remember { mutableStateOf<String?>(null) }
  val parser = remember { NewTimetableFormParser() }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("新建课程表") },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
      ) {
        OutlinedTextField(name, { name = it }, label = { Text("课表名称") }, singleLine = true)
        OutlinedTextField(
          startDate,
          { startDate = it },
          label = { Text("第一周周一（YYYY-MM-DD）") },
          singleLine = true,
        )
        OutlinedTextField(totalWeeks, { totalWeeks = it }, label = { Text("总周数") }, singleLine = true)
        OutlinedTextField(
          periods,
          { periods = it },
          label = { Text("每天节次时间（每行一节）") },
          supportingText = { Text("例如 08:00-09:35，可自由增删行") },
          minLines = 4,
        )
        error?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          runCatching { parser(name, startDate, totalWeeks, periods) }
            .onSuccess(onCreate)
            .onFailure { error = it.message ?: "课程表信息无效" }
        },
      ) { Text("创建") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
  )
}

private val DEFAULT_PERIODS =
  """
  08:00-09:35
  10:00-11:35
  14:00-15:35
  15:50-17:25
  """.trimIndent()
