package com.did2818.timetable.presentation.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.TimetableSnapshot
import com.did2818.timetable.domain.model.WeekScheduleItem
import com.did2818.timetable.domain.model.ReminderSettings
import com.did2818.timetable.domain.model.ReminderScope
import com.did2818.timetable.domain.repository.TimetableImporter
import com.did2818.timetable.domain.repository.TimetableExporter
import com.did2818.timetable.domain.repository.TimetableRepository
import com.did2818.timetable.domain.usecase.ClassEdit
import com.did2818.timetable.domain.usecase.CreateEmptyTimetable
import com.did2818.timetable.domain.usecase.EditTimetable
import com.did2818.timetable.domain.usecase.FindScheduleConflicts
import com.did2818.timetable.domain.usecase.NewTimetableSpec
import com.did2818.timetable.domain.usecase.TimetableLayoutEdit
import com.did2818.timetable.domain.usecase.UpdateTimetableLayout
import com.did2818.timetable.domain.usecase.UpdateReminderSettings
import com.did2818.timetable.domain.usecase.UpdateSplitDisplaySlot
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
  private val exporter: TimetableExporter = TimetableExporter { error("未配置导出器") },
  private val onImported: () -> Unit = {},
  private val clock: Clock = Clock.systemDefaultZone(),
  initialWeek: Int? = null,
  private val stateFactory: MainScreenStateFactory = MainScreenStateFactory(),
  private val editor: EditTimetable = EditTimetable(),
  private val createEmptyTimetable: CreateEmptyTimetable = CreateEmptyTimetable(),
  private val updateTimetableLayout: UpdateTimetableLayout = UpdateTimetableLayout(),
  private val updateReminderSettings: UpdateReminderSettings = UpdateReminderSettings(),
  private val updateSplitDisplaySlot: UpdateSplitDisplaySlot = UpdateSplitDisplaySlot(),
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

  fun selectWeek(week: Int) {
    if (week in 1..repository.timetable.value.term.totalWeeks) selectedWeek.value = week
  }

  fun stateForWeek(week: Int): MainScreenUiState = stateFactory.create(repository.timetable.value, week)

  private val messageChannel = Channel<String>(Channel.BUFFERED)
  val messages = messageChannel.receiveAsFlow()
  private val conflictChannel = Channel<String>(Channel.BUFFERED)
  val conflictWarnings = conflictChannel.receiveAsFlow()
  private val reminderPermissionChannel = Channel<Unit>(Channel.BUFFERED)
  val reminderPermissionRequests = reminderPermissionChannel.receiveAsFlow()
  private var copiedItem: WeekScheduleItem? = null

  fun importDocument(document: String) {
    viewModelScope.launch {
      runCatching { importer.import(document) }
        .onSuccess { timetable ->
          selectedWeek.value = timetable.term.weekNumberOn(LocalDate.now(clock)) ?: 1
          onImported()
          if (timetable.hasEnabledReminders()) reminderPermissionChannel.send(Unit)
          val conflictCount = FindScheduleConflicts()(timetable.meetings).size
          if (conflictCount > 0) conflictChannel.send("已导入课表，其中有 $conflictCount 处时间冲突。")
          else messageChannel.send("已导入 ${timetable.courses.size} 门课程")
        }
        .onFailure { error ->
          messageChannel.send(error.message ?: "课表导入失败")
        }
    }
  }

  fun reportFileReadError(error: Throwable) { messageChannel.trySend(error.message ?: "无法读取课表文件") }

  fun reportExportSuccess() { messageChannel.trySend("课表已导出") }

  fun exportDocument(): String {
    val timetable = repository.timetable.value
    require(timetable.periods.isNotEmpty()) { "请先新建或导入课程表" }
    return exporter.export(timetable)
  }

  fun createTimetable(spec: NewTimetableSpec) {
    viewModelScope.launch {
      runCatching {
          val timetable = createEmptyTimetable(spec)
          repository.replace(timetable)
          selectedWeek.value = timetable.term.weekNumberOn(LocalDate.now(clock)) ?: 1
        }
        .onSuccess {
          onImported()
          messageChannel.send("新课程表已创建，可以点击格子添加课程")
        }
        .onFailure { error -> messageChannel.send(error.message ?: "无法创建课程表") }
    }
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

  fun updateLayout(edit: TimetableLayoutEdit, onSuccess: () -> Unit = {}) {
    save("课表设置已保存", onSuccess) { updateTimetableLayout(it, edit) }
  }

  fun updateReminderSettings(settings: ReminderSettings, onSuccess: () -> Unit = {}) {
    save("提醒设置已保存", onSuccess) { updateReminderSettings(it, settings) }
  }

  fun updateSplitDisplay(day: DayOfWeek, periodNumber: Int, enabled: Boolean) {
    save("格子显示方式已保存", reportConflicts = false) {
      updateSplitDisplaySlot(it, day, periodNumber, enabled)
    }
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

  private fun save(
    message: String,
    onSuccess: () -> Unit = {},
    reportConflicts: Boolean = true,
    change: (TimetableSnapshot) -> TimetableSnapshot,
  ) {
    viewModelScope.launch {
      runCatching {
          val updated = change(repository.timetable.value)
          repository.replace(updated)
          FindScheduleConflicts()(updated.meetings).size
        }
        .onSuccess { conflictCount ->
          onImported()
          onSuccess()
          if (reportConflicts && conflictCount > 0) {
            conflictChannel.send("$message，但当前课表有 $conflictCount 处时间冲突。冲突格会显示 !。")
          } else messageChannel.send(message)
        }
        .onFailure { error -> messageChannel.send(error.message ?: "课表保存失败") }
    }
  }
}

private fun TimetableSnapshot.hasEnabledReminders(): Boolean =
  reminderSettings.scope != ReminderScope.DISABLED ||
    meetings.any { it.reminderOverride?.enabled == true }
