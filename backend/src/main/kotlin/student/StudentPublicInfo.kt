package student

import auth.GoogleUser
import com.google.cloud.datastore.Key
import course.StudentCourse
import partner.StudentRatingAccumulator

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
 * @property averageRating average rating of the student.
 * @property pastCourses a list of keys of past courses.
 * @property currCourses a list of keys of current courses.
 * @property grade the grade of the student, which means different things in different context.
 */
data class StudentPublicInfo(
        val id: Key, val name: String, val email: String, val picture: String,
        val skills: String, val introduction: String, val experience: String,
        val averageRating: Double, val pastCourses: List<Key>, val currCourses: List<Key>,
        val grade: Double = 3.0
) {

    companion object {

        /**
         * [buildForGeneral] returns a built [StudentPublicInfo] for general purpose.
         */
        fun buildForGeneral(studentId: Key): StudentPublicInfo? {
            val user = GoogleUser.getByKey(key = studentId) ?: return null
            val averageRating = StudentRatingAccumulator.getAverageRating(studentId = studentId)
            val pastCourses = StudentCourse.getAllPastCourseKeys(id = studentId)
            val currCourses = StudentCourse.getAllCurrCourseKeys(id = studentId)
            return StudentPublicInfo(
                    id = studentId, name = user.name, email = user.email, picture = user.picture,
                    skills = user.skills, introduction = user.introduction,
                    experience = user.experience,
                    averageRating = averageRating, pastCourses = pastCourses, currCourses = currCourses
            )
        }

    }

}
