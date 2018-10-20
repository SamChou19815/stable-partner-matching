package course

import api.cornell.data.classes.Subject
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [CourseInfo] is the static information about all the courses.
 *
 * @property key key of the course.
 * @property subject subject of the course. e.g. "CS"
 * @property code code of the course. e.g. "2112"
 * @property title title of the course. e.g. "Algo"
 * @property description the description of the the course. e.g. Introduction to bla.
 * @property weightVector the string form of the hashtable based vector.
 */
data class CourseInfo(
        val key: Key? = null, val subject: Subject = Subject.CS,
        val code: String, val title: String,
        val description: String, val weightVector: String
) {

    private object Table : TypedTable<Table>(tableName = "CourseInfo") {
        val subject = enumProperty(name = "subject", clazz = Subject::class.java)
        val code = stringProperty(name = "code")
        val title = stringProperty(name = "title")
        val description = longStringProperty(name = "description")
        val weightVector = longStringProperty(name = "weight_vector")
    }

    private class CourseInfoEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val subject: Subject = Table.subject.delegatedValue
        val code: String = Table.code.delegatedValue
        val title: String = Table.title.delegatedValue
        val description: String = Table.description.delegatedValue
        val weightVector: String = Table.weightVector.delegatedValue

        val asCourseInfo: CourseInfo
            get() = CourseInfo(
                    key = key, subject = subject, code = code, title = title,
                    description = description, weightVector = weightVector
            )

        val asSimplifiedCourseInfo: SimplifiedCourseInfo
            get() = SimplifiedCourseInfo(
                    key = key, subject = subject, code = code, title = title
            )

        companion object : TypedEntityCompanion<Table, CourseInfoEntity>(table = Table) {

            override fun create(entity: Entity): CourseInfoEntity = CourseInfoEntity(entity)


        }

    }

    companion object {

        /**
         * [get] returns an optional course information given the id of the course.
         */
        operator fun get(key: Key): CourseInfo? = CourseInfoEntity[key]?.asCourseInfo

        /**
         * [getSimplified] returns an optional simplified course information given the id of the
         * course.
         */
        fun getSimplified(key: Key): SimplifiedCourseInfo? =
                CourseInfoEntity[key]?.asSimplifiedCourseInfo

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
                table.subject gets courseInfo.subject
                table.code gets courseInfo.code
                table.title gets courseInfo.title
                table.description gets courseInfo.description
                table.weightVector gets courseInfo.weightVector
            }
        }

        /**
         * [removeAll] removes all course info from the database, useful for experimentation.
         */
        fun removeAll(): Unit = CourseInfoEntity.deleteAll()

    }
}