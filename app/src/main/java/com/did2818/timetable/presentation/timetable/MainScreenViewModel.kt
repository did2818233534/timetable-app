package com.did2818.timetable.presentation.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.domain.usecase.ClassEdit
import com.did2818.timetable.domain.usecase.EditTimetable
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(
  private val repository: TimetableRepository,
  private val importer: TimetableImporter,
  private val onImported: () -> Unit = {},
  private val clock: Clock = Clock.systemDefaultZone(),
  initialWeek: Int? = null,
  private val stateFactory: MainScreenStateFactory = MainScreenStateFactory(),
  private val editor: EditTimetable = EditTimetable(),
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
  private var copiedItem: WeekScheduleItem? = null

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

  fun addMeeting(day: DayOfWeek, period: ClassPeriod, edit: ClassEdit) {
    save("课程已添加") { editor.add(it, day, period.number, edit) }
  }

  fun updateMeeting(item: WeekScheduleItem, edit: ClassEdit) {
    save("课程已保存") { editor.update(it, item.meeting.id, edit) }
  }

  fun deleteMeeting(item: WeekScheduleItem) {
    save("课程已删除") { editor.delete(it, item.meeting.id) }
  }

  fun copyMeeting(item: WeekScheduleItem) {
    copiedItem = item
    messageChannel.trySend("已复制“${item.course.name}”，长按空白格可粘贴")
  }

  fun pasteMeeting(day: DayOfWeek, period: ClassPeriod) {
    val copied = copiedItem
    if (copied == null) {
      messageChannel.trySend("请先长按一门课程进行复制")
      return
    }
    save("课程已粘贴") { editor.paste(it, copied, day, period.number) }
  }

  private fun save(message: String, change: (TimetableSnapshot) -> TimetableSnapshot) {
    viewModelScope.launch {
      runCatching { repository.replace(change(repository.timetable.value)) }
        .onSuccess {
          onImported()
          messageChannel.send(message)
        }
        .onFailure { error -> messageChannel.send(error.message ?: "课表保存失败") }
    }
  }
}
