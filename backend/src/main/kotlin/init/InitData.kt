package init

import auth.GoogleUser
import course.CourseInfo
import course.SimplifiedCourseInfo
import course.StudentCourse
import freetime.StudentFreeTimeRecord
import partner.PartnerInvitation
import partner.StudentPartnership

/**
 * [InitData] is the initial data fetched by the client to ensure client can work.
 */
data class InitData(
        val allCourses: List<SimplifiedCourseInfo>,
        val profile: GoogleUser,
        val freeTimes: StudentFreeTimeRecord,
        val courses: List<StudentCourse>,
        val partners: List<StudentPartnership>,
        val partnerInvitations: List<PartnerInvitation>
) {

    companion object {

        /**
         * [getByUser] returns the information by the given [user].
         */
        fun getByUser(user: GoogleUser): InitData {
            val key = user.keyNotNull
            return InitData(
                    allCourses = CourseInfo.getAllSimplified(),
                    profile = user,
                    freeTimes = StudentFreeTimeRecord.getByStudentId(studentId = key),
                    courses = StudentCourse.getAllCoursesByStudentId(id = key),
                    partners = StudentPartnership.getAllPartnerships(studentId = key),
                    partnerInvitations = PartnerInvitation.getAllInvitations(invitedId = key)
            )
        }

    }


}
