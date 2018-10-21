package course

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import common.TimeStatus
import course.StudentCourse.Table.studentId
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [StudentCourse] represents a record of a student course.
 *
 * @property key key of the course record.
 * @property studentId the id of the student.
 * @property courseId the id of the course.
 * @property score score of the student.
 * @property status time status of the course.
 * @property isTa whether the student has become a TA.
 */
data class StudentCourse(
        val key: Key? = null,
        val studentId: Key, val courseId: Key,
        val score: Long?, val status: TimeStatus,
        val isTa: Boolean?
) {

    private object Table : TypedTable<Table>(tableName = "StudentCourse") {
        val studentId = keyProperty(name = "student_id")
        val courseId = keyProperty(name = "course_id")
        val score = nullableLongProperty(name = "score")
        val status = enumProperty(name = "status", clazz = TimeStatus::class.java)
        val isTa = nullableBoolProperty(name = "is_ta")
    }

    private class StudentCourseEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val studentId: Key = Table.studentId.delegatedValue
        val courseId: Key = Table.courseId.delegatedValue
        val score: Long? = Table.score.delegatedValue
        val status: TimeStatus = Table.status.delegatedValue
        val isTa: Boolean? = Table.isTa.delegatedValue

        val asStudentCourse: StudentCourse
            get() = StudentCourse(
                    key = key, studentId = studentId, courseId = courseId,
                    score = score, status = status, isTa = isTa
            )

        companion object : TypedEntityCompanion<Table, StudentCourseEntity>(table = Table) {

            override fun create(entity: Entity): StudentCourseEntity = StudentCourseEntity(entity)

        }

    }

    /**
     * [upsert] upserts the record into the database and returns the upserted one.
     */
    fun upsert(): StudentCourse {
        val entityOpt = StudentCourseEntity.query {
            filter {
                table.studentId eq studentId
                table.courseId eq courseId
            }
        }.firstOrNull()
        return StudentCourseEntity.upsert(entity = entityOpt) {
            table.studentId gets studentId
            table.courseId gets courseId
            table.score gets score
            table.status gets status
            table.isTa gets isTa
        }.asStudentCourse
    }

    /**
     * [delete] deletes the record into the database.
     */
    fun delete() {
        key?.let { StudentCourseEntity.delete(key = it) }
    }

    companion object {

        /**
         * [getAllStudentCourses] returns a map of all student courses with student id and course
         * id as keys..
         */
        @JvmStatic
        fun getAllStudentCourses(): Map<Key, Map<Key, StudentCourse>> =
                StudentCourseEntity.all()
                        .map { it.asStudentCourse }
                        .groupBy { it.studentId }
                        .map { (studentId, courses) ->
                            studentId to courses.groupBy { c -> c.courseId }
                                    .mapValues { (_, lst) -> lst.first() }
                        }
                        .toMap()

        /**
         * [getAllCoursesByStudentId] returns a list of all courses belongs to a student given
         * his [id].
         */
        @JvmStatic
        fun getAllCoursesByStudentId(id: Key): List<StudentCourse> =
                StudentCourseEntity.query {
                    filter { table.studentId eq id }
                    order { table.status.asc() }
                }.map { it.asStudentCourse }.toList()

        /**
         * [getAllCoursesByStudentAndCourseId] returns a list of all courses
         * with [studentId] and [courseId].
         */
        @JvmStatic
        fun getAllCoursesByStudentAndCourseId(studentId: Key, courseId: Key): StudentCourse? =
                StudentCourseEntity.query {
                    filter {
                        table.studentId eq studentId
                        table.courseId eq courseId
                    }
                    order { table.status.asc() }
                }.firstOrNull()?.asStudentCourse

        /**
         * [getAllPastCourseKeys] returns a list of all past courses' keys of a student with
         * [studentId].
         */
        fun getAllPastCourseKeys(studentId: Key): List<Key> =
                StudentCourseEntity.query {
                    filter {
                        table.studentId eq studentId
                        table.status eq TimeStatus.PAST
                    }
                }.map { it.courseId }.toList()

        /**
         * [getAllCurrCourseKeys] returns a list of all current courses' keys of a student
         * with [studentId].
         */
        fun getAllCurrCourseKeys(studentId: Key): List<Key> =
                StudentCourseEntity.query {
                    filter {
                        table.studentId eq studentId
                        table.status eq TimeStatus.CURRENT
                    }
                }.map { it.courseId }.toList()

        /**
         * [batchImport] imports the courses in batch.
         */
        fun <T> batchImport(source: List<T>, mapper: (T) -> StudentCourse) {
            StudentCourseEntity.batchInsert(source = source.map(mapper)) { course ->
                table.studentId gets course.studentId
                table.courseId gets course.courseId
                table.score gets course.score
                table.status gets course.status
                table.isTa gets course.isTa
            }
        }

        /**
         * [deleteAll] deletes all courses.
         */
        fun deleteAll() {
            // To overcome the API limitation of 500 operations
            val keys = StudentCourseEntity.allKeys().toList()
            for (key in keys) {
                StudentCourseEntity.delete(key)
            }
        }

    }

}