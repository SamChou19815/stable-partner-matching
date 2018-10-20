package partner

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable


/**
 * [StudentRatingRecord] is a record for one rating.
 *
 * @property ratingStudentId the student id that does rating.
 * @property ratedStudentId the student id that get rated.
 * @property courseId the id of the course as the rating context.
 * @property rating the actual rating, from 1 to 5.
 */
data class StudentRatingRecord(
        val ratingStudentId: Key, val ratedStudentId: Key, val courseId: Key, val rating: Long
) {

    private object Table : TypedTable<Table>(tableName = "StudentRatingAccumulator") {
        val ratingStudentId = keyProperty(name = "rating_student_id")
        val ratedStudentId = keyProperty(name = "rated_student_id")
        val courseId = keyProperty(name = "course_id")
        val rating = longProperty(name = "rating")
    }

    private class StudentRatingRecordEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val ratingStudentId: Key = Table.ratingStudentId.delegatedValue
        val ratedStudentId: Key = Table.ratedStudentId.delegatedValue
        val courseId: Key = Table.courseId.delegatedValue
        val rating: Long = Table.rating.delegatedValue

        val asStudentRatingRecord: StudentRatingRecord
            get() = StudentRatingRecord(
                    ratingStudentId = ratingStudentId, ratedStudentId = ratedStudentId,
                    courseId = courseId, rating = rating
            )

        companion object : TypedEntityCompanion<Table, StudentRatingRecordEntity>(table = Table) {

            override fun create(entity: Entity): StudentRatingRecordEntity =
                    StudentRatingRecordEntity(entity = entity)

        }
    }

    companion object {

        /**
         * [getAllRatingRecordByStudentId] returns a list of all rating records for one rater with
         * [id].
         */
        fun getAllRatingRecordByStudentId(id: Key): List<StudentRatingRecord> =
                StudentRatingRecordEntity.query {
                    filter { table.ratingStudentId eq id }
                }.map { it.asStudentRatingRecord }.toList()

        /**
         * [editRating] edits the rating according to the given [record].
         */
        fun editRating(record: StudentRatingRecord) {
            val entityOpt = StudentRatingRecordEntity.query {
                filter { table.ratingStudentId eq record.ratingStudentId }
            }.firstOrNull()
            StudentRatingRecordEntity.upsert(entity = entityOpt) {
                table.ratingStudentId gets record.ratingStudentId
                table.ratedStudentId gets record.ratedStudentId
                table.courseId gets record.courseId
                table.rating gets record.rating
            }
            if (entityOpt == null) {
                StudentRatingAccumulator.addRating(
                        ratedStudentId = record.ratedStudentId, rating = record.rating
                )
            } else {
                StudentRatingAccumulator.editRating(
                        ratedStudentId = record.ratedStudentId,
                        oldRating = entityOpt.rating, newRating = record.rating
                )
            }
        }

    }

}