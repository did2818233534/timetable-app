package com.did2818.timetable.domain.model

/** Course-level information shared by one or more weekly meetings. */
data class Course(
  val id: String,
  val name: String,
  val teacher: String = "",
  val colorArgb: Long = DEFAULT_COLOR_ARGB,
  val note: String = "",
) {
  init {
    require(id.isNotBlank()) { "Course id cannot be blank" }
    require(name.isNotBlank()) { "Course name cannot be blank" }
    require(colorArgb in 0x00000000L..0xFFFFFFFFL) { "Color must be a 32-bit ARGB value" }
  }

  private companion object {
    const val DEFAULT_COLOR_ARGB = 0xFF4F6BEDL
  }
}
