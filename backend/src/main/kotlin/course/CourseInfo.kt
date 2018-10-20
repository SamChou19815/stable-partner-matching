package course

import com.google.cloud.datastore.Entity
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [CourseInfo] is the static information about all the courses.
 *
 * @property id id of the course.
 * @property subject subject of the course. e.g. "CS"
 * @property code code of the course. e.g. "2112"
 * @property title title of the course. e.g. "Algo"
 * @property description the description of the the course. e.g. Introduction to bla.
 * @property weightVector the string form of the hashtable based vector.
 */
data class CourseInfo(
        val id: Long, val subject: String, val code: String, val title: String,
        val description: String, val weightVector: String
) {

    private object Table : TypedTable<Table>(tableName = "CourseInfo") {
        val id = longProperty(name = "id")
        val subject = stringProperty(name = "subject")
        val code = stringProperty(name = "code")
        val title = stringProperty(name = "title")
        val description = longStringProperty(name = "description")
        val weightVector = longStringProperty(name = "weight_vector")
    }

    private class CourseInfoEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val id: Long = Table.id.delegatedValue
        val subject: String = Table.subject.delegatedValue
        val code: String = Table.code.delegatedValue
        val title: String = Table.title.delegatedValue
        val description: String = Table.description.delegatedValue
        val weightVector: String = Table.weightVector.delegatedValue

        val asCourseInfo: CourseInfo
            get() = CourseInfo(
                    id = id, subject = subject, code = code, title = title,
                    description = description, weightVector = weightVector
            )

        val asSimplifiedCourseInfo: SimplifiedCourseInfo
            get() = SimplifiedCourseInfo(
                    id = id, subject = subject, code = code, title = title
            )

        companion object : TypedEntityCompanion<Table, CourseInfoEntity>(table = Table) {

            override fun create(entity: Entity): CourseInfoEntity = CourseInfoEntity(entity)


        }

    }

    companion object {

        /**
         * [getById] returns an optional course information given the id of the course.
         */
        fun getById(id: Long): CourseInfo? =
                CourseInfoEntity.query {
                    filter { table.id eq id }
                }.firstOrNull()?.asCourseInfo

        /**
         * [getAll] returns a list of all [CourseInfo] in the database.
         */
        fun getAll(): List<CourseInfo> =
                CourseInfoEntity.all().map { it.asCourseInfo }.toList()

        /**
         * [getAll] returns a list of all [CourseInfo] in the database.
         */
        fun getAllSimplified(): List<SimplifiedCourseInfo> =
                CourseInfoEntity.all().map { it.asSimplifiedCourseInfo }.toList()

        /**
         * [addAll] adds a list of all the courses in the database.
         */
        fun addAll(list: List<CourseInfo>) {
            CourseInfoEntity.batchInsert(source = list) { courseInfo ->
                table.id gets courseInfo.id
                table.subject gets courseInfo.subject
                table.code gets courseInfo.code
                table.title gets courseInfo.title
                table.description gets courseInfo.description
                table.weightVector gets courseInfo.weightVector
            }
        }

    }
}