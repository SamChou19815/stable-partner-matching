package staticprocessor

import api.cornell.data.classes.Subject
import auth.GoogleUser
import com.google.cloud.datastore.Key
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import common.StudentClass
import common.TimeStatus
import course.CourseInfo
import course.StudentCourse
import java.io.InputStreamReader

/**
 * [StaticProcessor] processes the static file in resources.
 */
object StaticProcessor {

    /**
     * [importAllCourses] imports all courses
     */
    fun importAllCourses() {
        val gsonSimpleConverter = Gson()
        val gson = GsonBuilder().registerTypeAdapter(
                CourseInfo::class.java, JsonDeserializer<CourseInfo> { json, _, _ ->
            val obj = json.asJsonObject
            val weightVectorJson = obj["weightVector"]
            CourseInfo(
                    subject = Subject.valueOf(obj["subject"].asString),
                    code = obj["code"].asString,
                    title = obj["title"].asString,
                    description = obj["description"]?.asString ?: "",
                    weightVector = gsonSimpleConverter.toJson(weightVectorJson)
            )
        }).create()
        javaClass.getResourceAsStream("course-info.json")
                .let(block = ::InputStreamReader)
                .let { reader ->
                    val courses: List<CourseInfo> = gson.fromJson(
                            reader, object : TypeToken<List<CourseInfo>>() {}.type
                    )
                    courses
                }
                .let(block = CourseInfo.Companion::addAll)
    }

    /**
     * [RandomUserCourse] the course taken by the random user.
     */
    private data class RandomUserCourse(val courseName: String, val grade: Long) {

        fun toStudentCourse(studentId: Key, timeStatus: TimeStatus): StudentCourse {
            val code = courseName.substring(startIndex = 3)
            val courseInfo = CourseInfo.getBySubjectAndCode(subject = Subject.CS, code = code)
            val isTaProb = when (grade) {
                12L -> 0.3
                11L -> 0.1
                else -> 0.0
            }
            val isTa = Math.random() > isTaProb
            return StudentCourse(
                    studentId = studentId, courseId = courseInfo.key!!,
                    score = grade.takeIf { it >= 0 }, status = timeStatus, isTa = isTa
            )
        }

    }

    /**
     * [RandomUser] the random user object.
     */
    private data class RandomUser(
            val uid: String = "", val name: String = "", val email: String = "",
            val picture: String = "", val graduationYear: Long = 0,
            val pastCourses: List<RandomUserCourse> = emptyList(),
            val currentCourses: List<RandomUserCourse> = emptyList(),
            val futureCourses: List<RandomUserCourse> = emptyList()
    ) {

        fun toGoogleUser(): GoogleUser {
            val clazz = when (graduationYear) {
                2022L -> StudentClass.FRESHMAN
                2021L -> StudentClass.SOPHOMORE
                2020L -> StudentClass.JUNIOR
                2019L -> StudentClass.SENIOR
                else -> error(message = "Bad Input $graduationYear. $this")
            }
            return GoogleUser(
                    uid = uid, name = name, email = email, picture = picture,
                    studentClass = clazz, graduationYear = graduationYear
            ).upsert()
        }

    }

    /**
     * [importAllRandomUsers] imports all random users.
     */
    fun importAllRandomUsers() {
        val insertedRandomUsers = javaClass
                .getResourceAsStream("random-user.json")
                .let(block = ::InputStreamReader)
                .let { reader ->
                    val rawRandomUsers: List<RandomUser> = Gson().fromJson(
                            reader, object : TypeToken<List<RandomUser>>() {}.type
                    )
                    rawRandomUsers
                }
                .map { it to it.toGoogleUser() }
        for ((raw, google) in insertedRandomUsers) {
            val studentId = google.keyNotNull
            val coursesWithTime = arrayListOf<Pair<RandomUserCourse, TimeStatus>>()
            raw.pastCourses.forEach { coursesWithTime.add(it to TimeStatus.PAST) }
            raw.currentCourses.forEach { coursesWithTime.add(it to TimeStatus.CURRENT) }
            raw.futureCourses.forEach { coursesWithTime.add(it to TimeStatus.FUTURE) }
            StudentCourse.batchImport(source = coursesWithTime) { (course, time) ->
                course.toStudentCourse(studentId = studentId, timeStatus = time)
            }
        }
    }

}
