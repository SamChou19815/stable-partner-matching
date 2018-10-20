package partner

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [StudentRatingAccumulator] is a simple accumulator for student's past ratings.
 *
 * @property ratedStudentId the student to be rated.
 * @property totalRating the total rating of the student.
 * @property totalRating the total number of raters.
 */
data class StudentRatingAccumulator(
        val ratedStudentId: Key, val totalRating: Long, val totalRater: Long
) {

    private object Table : TypedTable<Table>() {
        val ratedStudentId = keyProperty(name = "rated_student_id")
        val totalRating = longProperty(name = "total_rating")
        val totalRater = longProperty(name = "total_rater")
    }

    private class StudentRatingAccEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val ratedStudentId: Key = Table.ratedStudentId.delegatedValue
        val totalRating: Long = Table.totalRating.delegatedValue
        val totalRater: Long = Table.totalRater.delegatedValue

        val asStudentRatingAccumulator: StudentRatingAccumulator
            get() = StudentRatingAccumulator(
                    ratedStudentId = ratedStudentId,
                    totalRating = totalRating,
                    totalRater = totalRater
            )

        companion object : TypedEntityCompanion<Table, StudentRatingAccEntity>(table = Table) {

            override fun create(entity: Entity): StudentRatingAccEntity =
                    StudentRatingAccEntity(entity = entity)

        }

    }

    companion object {

        /**
         * [getAverageRating] returns the average rating for a specific student given the
         * [studentId].
         */
        fun getAverageRating(studentId: Key): Double =
                StudentRatingAccEntity.query {
                    filter { table.ratedStudentId eq studentId }
                }.firstOrNull()?.let { 1.0 * it.totalRating / it.totalRater } ?: 3.0

        /**
         * [addRating] adds a rating for a specific student [ratedStudentId] with [rating].
         */
        fun addRating(ratedStudentId: Key, rating: Long) {
            val entityOpt = StudentRatingAccEntity.query {
                filter { table.ratedStudentId eq ratedStudentId }
            }.firstOrNull()
            if (entityOpt == null) {
                StudentRatingAccEntity.insert {
                    table.ratedStudentId gets ratedStudentId
                    table.totalRating gets rating
                    table.totalRater gets 1L
                }
            } else {
                StudentRatingAccEntity.upsert(entity = entityOpt) {
                    table.totalRating gets (entityOpt.totalRating + rating)
                    table.totalRater gets (entityOpt.totalRater + 1)
                }
            }
        }

        /**
         * [editRating] edits a rating for a specific student [ratedStudentId], replacing
         * [oldRating] with [newRating].
         */
        fun editRating(ratedStudentId: Key, oldRating: Long, newRating: Long) {
            val entityOpt = StudentRatingAccEntity.query {
                filter { table.ratedStudentId eq ratedStudentId }
            }.first()
            StudentRatingAccEntity.upsert(entity = entityOpt) {
                table.totalRating gets (entityOpt.totalRating - oldRating + newRating)
            }
        }

    }

}