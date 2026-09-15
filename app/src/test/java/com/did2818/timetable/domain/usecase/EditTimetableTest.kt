package com.did2818.timetable.domain.usecase

import com.did2818.timetable.data.sample.SampleTimetableRepository
import com.did2818.timetable.domain.model.WeekParity
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.ReminderDelivery
import com.did2818.timetable.domain.model.ReminderOverride
import com.did2818.timetable.domain.usecase.BuildWeekSchedule
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test

class EditTimetableTest {
  private var id = 0
  private val editor = EditTimetable { (++id).toString() }
  private val timetable = SampleTimetableRepository().timetable.value

  @Test
  fun update_changesCourseNoteAndMeetingRules() {
    val meeting = timetable.meetings.first()
    val updated =
      editor.update(
        timetable,
        meeting.id,
        ClassEdit("新课程名", "B202，带实验服", WeekPattern(3, 8, WeekParity.ODD_WEEKS)),
      )

    assertEquals("新课程名", updated.courses.single { it.id == meeting.courseId }.name)
    assertEquals("B202，带实验服", updated.meetings.single { it.id == meeting.id }.note)
  }

  @Test
  fun paste_placesCopiedClassInTargetCell() {
    val copied = BuildWeekSchedule()(2, timetable.courses, timetable.meetings).first()
    val updated = editor.paste(timetable, copied, DayOfWeek.SATURDAY, 5)
    val pasted = updated.meetings.last()

    assertEquals(DayOfWeek.SATURDAY, pasted.dayOfWeek)
    assertEquals(timetable.periods.last().startTime, pasted.startTime)
    assertEquals(copied.course.id, pasted.courseId)
  }

  @Test
  fun add_usesSelectedDayPeriodAndNote() {
    val updated =
      editor.add(
        timetable,
        DayOfWeek.SATURDAY,
        5,
        ClassEdit("实验课", "C303，自带电脑", WeekPattern(2, 6)),
      )
    val meeting = updated.meetings.last()

    assertEquals("C303，自带电脑", meeting.note)
    assertEquals(DayOfWeek.SATURDAY, meeting.dayOfWeek)
    assertEquals(timetable.periods.last().endTime, meeting.endTime)
  }

  @Test
  fun paste_preservesConflictingClassForUserResolution() {
    val copied = BuildWeekSchedule()(2, timetable.courses, timetable.meetings).first()
    val occupied = timetable.meetings.first { it.id != copied.meeting.id }
    val period = timetable.periods.single { it.startTime == occupied.startTime }

    val updated = editor.paste(timetable, copied, occupied.dayOfWeek, period.number)

    assertEquals(1, FindScheduleConflicts()(updated.meetings).size)
  }

  @Test
  fun update_savesPerMeetingReminderOverride() {
    val meeting = timetable.meetings.first()
    val reminder = ReminderOverride(true, 25, ReminderDelivery.ALARM)

    val updated =
      editor.update(
        timetable,
        meeting.id,
        ClassEdit("高等数学", "A101", meeting.weekPattern, reminder),
      )

    assertEquals(reminder, updated.meetings.single { it.id == meeting.id }.reminderOverride)
  }
}
