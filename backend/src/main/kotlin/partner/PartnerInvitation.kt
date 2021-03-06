package partner

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import common.TimeStatus
import student.StudentPublicInfo
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [PartnerInvitation] is a record for one partner invitation.
 *
 * @property key key of the invitation record.
 * @property inviterId the people that invites.
 * @property invitedId the people that gets invited.
 * @property courseId the id of the course involved.
 * @property timeStatus the time status for the invitation.
 */
data class PartnerInvitation(
        val key: Key? = null,
        val inviterId: Key,
        val inviterInfo: StudentPublicInfo? = null,
        val invitedId: Key,
        val courseId: Key,
        val timeStatus: TimeStatus
) {

    private object Table : TypedTable<Table>(tableName = "PartnerInvitation") {
        val inviterId = keyProperty(name = "inviter_id")
        val invitedId = keyProperty(name = "invited_id")
        val courseId = keyProperty(name = "course_id")
        val timeStatus = enumProperty(name = "time_status", clazz = TimeStatus::class.java)
    }

    private class PartnerInvitationEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val inviterId: Key = Table.inviterId.delegatedValue
        val invitedId: Key = Table.invitedId.delegatedValue
        val courseId: Key = Table.courseId.delegatedValue
        val timeStatus: TimeStatus = Table.timeStatus.delegatedValue

        val asPartnerInvitation: PartnerInvitation
            get() = PartnerInvitation(
                    inviterId = inviterId, invitedId = invitedId,
                    courseId = courseId, timeStatus = timeStatus
            )

        companion object : TypedEntityCompanion<Table, PartnerInvitationEntity>(table = Table) {

            override fun create(entity: Entity): PartnerInvitationEntity =
                    PartnerInvitationEntity(entity = entity)

        }
    }


    /**
     * [delete] deletes this invitation] from the system.
     */
    internal fun delete() {
        PartnerInvitationEntity.query {
            filter {
                table.inviterId eq inviterId
                table.invitedId eq invitedId
                table.courseId eq courseId
                table.timeStatus eq timeStatus
            }
        }.firstOrNull()?.key?.let { PartnerInvitationEntity.delete(key = it) }
    }

    companion object {

        /**
         * [getAllInvitations] returns a list of all invitations belongs to [invitedId].
         */
        fun getAllInvitations(invitedId: Key): List<PartnerInvitation> =
                PartnerInvitationEntity.query {
                    filter { table.invitedId eq invitedId }
                }.map { entity ->
                    val inv = entity.asPartnerInvitation
                    inv.copy(
                            inviterInfo = StudentPublicInfo.buildForGeneral(
                                    studentId = inv.inviterId
                            )
                    )
                }.toList()

        /**
         * [editPartnerInvitation] edits one partner invitation based on the given
         * [invitation].
         *
         * @return whether you can send the invitation.
         */
        fun editPartnerInvitation(invitation: PartnerInvitation): Boolean {
            val partnershipAlreadyExists = StudentPartnership.exists(
                    student1Id = invitation.inviterId,
                    student2Id = invitation.invitedId,
                    courseId = invitation.courseId
            )
            if (partnershipAlreadyExists) {
                return false
            }
            val entityOpt = PartnerInvitationEntity.query {
                filter {
                    table.inviterId eq invitation.inviterId
                    table.invitedId eq invitation.invitedId
                    table.courseId eq invitation.courseId
                }
            }.firstOrNull()
            PartnerInvitationEntity.upsert(entity = entityOpt) {
                table.inviterId gets invitation.inviterId
                table.invitedId gets invitation.invitedId
                table.courseId gets invitation.courseId
                table.timeStatus gets invitation.timeStatus
            }
            return true
        }

    }

}