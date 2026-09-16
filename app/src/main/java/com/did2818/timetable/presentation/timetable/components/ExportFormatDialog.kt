package com.did2818.timetable.presentation.timetable.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExportFormatDialog(
  onDismiss: () -> Unit,
  onExportExcel: () -> Unit,
  onExportJson: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("选择导出格式") },
    text = {
      Column {
        TextButton(onClick = onExportExcel, modifier = Modifier.fillMaxWidth()) {
          Text("Excel 课表（.xlsx）")
        }
        TextButton(onClick = onExportJson, modifier = Modifier.fillMaxWidth()) {
          Text("应用课表文件（.timetable.json）")
        }
        Text("Excel 适合查看和编辑；JSON 可以重新导入应用。")
      }
    },
    confirmButton = {},
    dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
  )
}
