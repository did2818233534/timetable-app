package com.did2818.timetable.presentation.timetable.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.presentation.theme.TimetableAppTheme
import com.did2818.timetable.presentation.timetable.MainScreen
import com.did2818.timetable.presentation.timetable.MainScreenStateFactory

private val previewState =
  MainScreenStateFactory().create(
    timetable = SampleTimetableRepository().timetable.value,
    requestedWeek = 2,
  )

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TimetableScreenPreview() {
  TimetableAppTheme {
    MainScreen(
      state = previewState,
      onPreviousWeek = {},
      onNextWeek = {},
      onImportClick = {},
      onEditItem = {},
      onCopyItem = {},
      onCreateCell = { _, _ -> },
      onPasteCell = { _, _ -> },
    )
  }
}
