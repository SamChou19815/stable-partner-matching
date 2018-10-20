package course

import api.cornell.data.classes.Subject
import com.google.cloud.datastore.Key

/**
 * [SimplifiedCourseInfo] is the simplified static information about all the courses.
 *
 * @property key key of the course.
 * @property subject subject of the course. e.g. "CS"
 * @property code code of the course. e.g. "2112"
 * @property title title of the course. e.g. "Algo"
 */
data class SimplifiedCourseInfo(
        val key: Key,
        val subject: Subject,
        val code: String,
        val title: String
)
