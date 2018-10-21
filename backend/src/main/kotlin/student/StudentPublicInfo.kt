package student

import auth.GoogleUser
import com.google.cloud.datastore.Key
import course.StudentCourse
import freetime.FreeTimeInterval

/**
 * [StudentPublicInfo] is the information that can be publicly displayed for the student.
 *
 * @property id id of the student.
 * @property name name of the student.
 * @property email email of the student.
 * @property picture picture of the student.
 * @property skills picture of the student.
 * @property introduction introduction of the student.
 * @property experience experience of the student.
 * @property freeTimes student's free times.
 * @property pastCourses a list of keys of past courses.
 * @property currCourses a list of keys of current courses.
 * @property grade the grade of the student, which means different things in different context.
 */
data class StudentPublicInfo(
        val id: Key, val name: String, val email: String, val picture: String,
        val skills: String? = null, val introduction: String? = null,
        val experience: String? = null, val freeTimes: List<FreeTimeInterval> = emptyList(),
        val pastCourses: List<Key> = emptyList(), val currCourses: List<Key> = emptyList(),
        val grade: Double = 3.0
) {

    companion object {

        /**
         * [buildForGeneral] returns a built [StudentPublicInfo] for general purpose.
         * We also allow you to disable [fullDetail].
         */
        fun buildForGeneral(studentId: Key, fullDetail: Boolean = true): StudentPublicInfo? {
            val user = GoogleUser.getByKey(key = studentId) ?: return null
            return if (fullDetail) {
                val pastCourses = StudentCourse.getAllPastCourseKeys(id = studentId)
                val currCourses = StudentCourse.getAllCurrCourseKeys(id = studentId)
                StudentPublicInfo(
                        id = studentId, name = user.name, email = user.email,
                        picture = user.picture, skills = user.skills,
                        introduction = user.introduction, experience = user.experience,
                        freeTimes = user.freeTimes,
                        pastCourses = pastCourses, currCourses = currCourses
                )
            } else StudentPublicInfo(
                    id = studentId, name = user.name,
                    email = user.email, picture = user.picture
            )
        }
    }
}
