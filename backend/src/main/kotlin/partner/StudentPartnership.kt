package partner

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import common.TimeStatus
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [StudentPartnership] records one student partnership record for a specific course.
 *
 * @property student1Id the id of the first student.
 * @property student2Id the id of the second student.
 * @property courseId the id of the course.
 * @property timeStatus the time status of the partnership.
 */
data class StudentPartnership(
        val student1Id: Key,
        val student2Id: Key,
        val courseId: Key,
        val timeStatus: TimeStatus
) {

    private object Table : TypedTable<Table>(tableName = "StudentPartnership") {
        val student1Id = keyProperty(name = "student1_id")
        val student2Id = keyProperty(name = "student2_id")
        val courseId = keyProperty(name = "course_id")
        val timeStatus = enumProperty(name = "time_status", clazz = TimeStatus::class.java)
    }

    private class StudentPartnershipEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val student1Id: Key = Table.student1Id.delegatedValue
        val student2Id: Key = Table.student2Id.delegatedValue
        val courseId: Key = Table.courseId.delegatedValue
        val timeStatus: TimeStatus = Table.timeStatus.delegatedValue

        val asStudentPartnership: StudentPartnership
            get() = StudentPartnership(
                    student1Id = student1Id, student2Id = student2Id,
                    courseId = courseId, timeStatus = timeStatus
            )

        companion object : TypedEntityCompanion<Table, StudentPartnershipEntity>(table = Table) {

            override fun create(entity: Entity): StudentPartnershipEntity =
                    StudentPartnershipEntity(entity = entity)

        }
    }

    /**
     * [removeSelf] removes this partnership from the database.
     */
    fun removeSelf() {
        val entityOpt1 = StudentPartnershipEntity.query {
            filter {
                table.student1Id eq student1Id
                table.student2Id eq student2Id
                table.courseId eq courseId
            }
        }.firstOrNull()
        val entityOpt2 = StudentPartnershipEntity.query {
            filter {
                table.student1Id eq student2Id
                table.student2Id eq student1Id
                table.courseId eq courseId
            }
        }.firstOrNull()
        val existingEntitiesKeys = arrayListOf<Key>()
        entityOpt1?.let { existingEntitiesKeys.add(it.key) }
        entityOpt2?.let { existingEntitiesKeys.add(it.key) }
        if (existingEntitiesKeys.isNotEmpty()) {
            StudentPartnershipEntity.delete(existingEntitiesKeys)
        }
    }

    companion object {

        /**
         * [exists] returns whether a partnership parameterized by [student1Id], [student2Id] and
         * [courseId] exists.
         */
        fun exists(student1Id: Key, student2Id: Key, courseId: Key): Boolean =
                StudentPartnershipEntity.any {
                    filter {
                        table.student1Id eq student1Id
                        table.student2Id eq student2Id
                        table.courseId eq courseId
                    }
                }

        /**
         * [getAllPartnerships] returns a list of all partnerships given a [studentId].
         */
        fun getAllPartnerships(studentId: Key): List<StudentPartnership> =
                StudentPartnershipEntity.query {
                    filter { table.student1Id eq studentId }
                    order { table.timeStatus.asc() }
                }.map { it.asStudentPartnership }.toList()

        /**
         * [handleInvitation] handles an invitation given the [invitation] and whether it's
         * [accepted].
         */
        fun handleInvitation(invitation: PartnerInvitation, accepted: Boolean) {
            invitation.delete()
            if (!accepted) {
                return
            }
            // Delete existing partnership
            val entityOpt1 = StudentPartnershipEntity.query {
                filter {
                    table.student1Id eq invitation.invitedId
                    table.student2Id eq invitation.inviterId
                    table.courseId eq invitation.courseId
                }
            }.firstOrNull()
            val entityOpt2 = StudentPartnershipEntity.query {
                filter {
                    table.student1Id eq invitation.inviterId
                    table.student2Id eq invitation.invitedId
                    table.courseId eq invitation.courseId
                }
            }.firstOrNull()
            val existingEntitiesKeys = arrayListOf<Key>()
            entityOpt1?.let { existingEntitiesKeys.add(it.key) }
            entityOpt2?.let { existingEntitiesKeys.add(it.key) }
            if (existingEntitiesKeys.isNotEmpty()) {
                StudentPartnershipEntity.delete(existingEntitiesKeys)
            }
            // Create new
            StudentPartnershipEntity.insert {
                table.student1Id gets invitation.inviterId
                table.student2Id gets invitation.invitedId
                table.courseId gets invitation.courseId
                table.timeStatus gets invitation.timeStatus
            }
            StudentPartnershipEntity.insert {
                table.student1Id gets invitation.invitedId
                table.student2Id gets invitation.inviterId
                table.courseId gets invitation.courseId
                table.timeStatus gets invitation.timeStatus
            }
        }

    }

}