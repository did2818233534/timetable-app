package com.did2818.timetable.presentation.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import java.time.Clock
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class MainScreenViewModel(
  private val repository: TimetableRepository,
  private val importer: TimetableImporter,
  private val onImported: () -> Unit = {},
  private val clock: Clock = Clock.systemDefaultZone(),
  initialWeek: Int? = null,
  private val stateFactory: MainScreenStateFactory = MainScreenStateFactory(),
) : ViewModel() {
  private val initialTimetable = repository.timetable.value
  private val startingWeek =
    initialWeek?.coerceIn(1, initialTimetable.term.totalWeeks)
      ?: initialTimetable.term.weekNumberOn(LocalDate.now(clock))
      ?: 1
  private val selectedWeek = MutableStateFlow(startingWeek)

  val uiState: StateFlow<MainScreenUiState> =
    combine(repository.timetable, selectedWeek, stateFactory::create)
      .stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        stateFactory.create(initialTimetable, startingWeek),
      )

  fun showPreviousWeek() = selectWeek(selectedWeek.value - 1)

  fun showNextWeek() = selectWeek(selectedWeek.value + 1)

  fun selectWeek(week: Int) {
    if (week in 1..repository.timetable.value.term.totalWeeks) selectedWeek.value = week
  }

  private val messageChannel = Channel<String>(Channel.BUFFERED)
  val messages = messageChannel.receiveAsFlow()

  fun importDocument(document: String) {
    viewModelScope.launch {
      runCatching { importer.import(document) }
        .onSuccess { timetable ->
          selectedWeek.value = timetable.term.weekNumberOn(LocalDate.now(clock)) ?: 1
          onImported()
          messageChannel.send("已导入 ${timetable.courses.size} 门课程")
        }
        .onFailure { error ->
          messageChannel.send(error.message ?: "课表导入失败")
        }
    }
  }

  fun reportFileReadError(error: Throwable) {
    messageChannel.trySend(error.message ?: "无法读取课表文件")
  }
}
