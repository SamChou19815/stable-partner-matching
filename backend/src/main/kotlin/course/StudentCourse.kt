package course

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import common.TimeStatus
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
        val score: Long, val status: TimeStatus,
        val isTa: Boolean
) {

    private object Table : TypedTable<Table>() {
        val studentId = keyProperty(name = "student_id")
        val courseId = keyProperty(name = "course_id")
        val score = longProperty(name = "score")
        val status = enumProperty(name = "status", clazz = TimeStatus::class.java)
        val isTa = boolProperty(name = "is_ta")
    }

    private class StudentCourseEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val studentId: Key = Table.studentId.delegatedValue
        val courseId: Key = Table.courseId.delegatedValue
        val score: Long = Table.score.delegatedValue
        val status: TimeStatus = Table.status.delegatedValue
        val isTa: Boolean = Table.isTa.delegatedValue

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
     * [upsert] upserts the record into the database.
     */
    fun upsert() {
        val entityOpt = key?.let { StudentCourseEntity[it] }
        StudentCourseEntity.upsert(entity = entityOpt) {
            table.studentId gets studentId
            table.courseId gets courseId
            table.score gets score
            table.status gets status
            table.isTa gets isTa
        }
    }

    /**
     * [delete] deletes the record into the database.
     */
    fun delete() {
        key?.let { StudentCourseEntity.delete(key = it) }
    }

    companion object {

        /**
         * [getAllCoursesByStudentId] returns a list of all courses belongs to a student given
         * his [id].
         */
        fun getAllCoursesByStudentId(id: Key): List<StudentCourse> =
                StudentCourseEntity.query {
                    filter { table.studentId eq id }
                    order { table.status.asc() }
                }.map { it.asStudentCourse }.toList()

        /**
         * [getAllPastCourseKeys] returns a list of all past courses key's of a student with [id].
         */
        fun getAllPastCourseKeys(id: Key): List<Key> =
                StudentCourseEntity.query {
                    filter {
                        table.studentId eq id
                        table.status eq TimeStatus.PAST
                    }
                }.map { it.key }.toList()

    }

}