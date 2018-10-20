package course

/**
 * [SimplifiedCourseInfo] is the simplified static information about all the courses.
 *
 * @property id id of the course.
 * @property subject subject of the course. e.g. "CS"
 * @property code code of the course. e.g. "2112"
 * @property title title of the course. e.g. "Algo"
 */
data class SimplifiedCourseInfo(
        val id: Long,
        val subject: String,
        val code: String,
        val title: String
)
