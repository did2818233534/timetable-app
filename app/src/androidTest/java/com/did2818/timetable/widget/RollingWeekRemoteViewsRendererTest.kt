package com.did2818.timetable.widget

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.did2818.timetable.domain.model.ClassMeeting
import com.did2818.timetable.domain.model.ClassPeriod
import com.did2818.timetable.domain.model.Course
import com.did2818.timetable.domain.model.DaySchedule
import com.did2818.timetable.domain.model.WeekPattern
import com.did2818.timetable.domain.model.WeekScheduleItem
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RollingWeekRemoteViewsRendererTest {
  private val context = ApplicationProvider.getApplicationContext<Context>()

  @Test
  fun render_twoCoursesInOneCell_inflatesBothEntries() {
    val items = listOf(item("first", "上半课程"), item("second", "下半课程"))
    val views =
      RollingWeekRemoteViewsRenderer(context)
        .render(listOf(period), listOf(DaySchedule(LocalDate.of(2026, 9, 16), listOf(items))))
        .apply(context, FrameLayout(context))

    assertEquals(1, views.findText("上半课程").size)
    assertEquals(1, views.findText("下半课程").size)
  }

  private fun View.findText(text: String): List<View> {
    val matches = ArrayList<View>()
    findViewsWithText(matches, text, View.FIND_VIEWS_WITH_TEXT)
    return matches
  }

  private fun item(id: String, name: String): WeekScheduleItem =
    WeekScheduleItem(
      course = Course(id, name),
      meeting =
        ClassMeeting(
          id = "$id-meeting",
          courseId = id,
          dayOfWeek = DayOfWeek.WEDNESDAY,
          startTime = period.startTime,
          endTime = period.endTime,
          weekPattern = WeekPattern(1, 18),
        ),
      isActive = true,
    )

  private companion object {
    val period = ClassPeriod(1, LocalTime.of(8, 0), LocalTime.of(9, 35))
  }
}
