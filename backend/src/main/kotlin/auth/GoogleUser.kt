package auth

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import common.StudentClass
import freetime.FreeTimeInterval
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * A [GoogleUser] is an class that contains many information of a Google user, obtained from a
 * Firebase token.
 *
 * @property key key of the user.
 * @property uid id the user.
 * @property name name of the user.
 * @property email email of the user.
 * @property picture picture of the user.
 * @property studentClass the class of the student.
 * @property graduationYear grad year of the student.
 * @property skills a list of skills given by the student.
 * @property introduction student's self-intro.
 * @property experience student's self-intro of experience.
 * @property freeTimes free time record.
 */
data class GoogleUser(
        val key: Key? = null, @field:Transient val uid: String,
        val name: String, val email: String, val picture: String,
        val studentClass: StudentClass = StudentClass.FRESHMAN,
        val graduationYear: Long = 2022, val skills: String = "break things",
        val introduction: String = "", val experience: String = "",
        val freeTimes: List<FreeTimeInterval> = emptyList()
) {

    /**
     * [keyNotNull] returns a Key that is definitely not null.
     */
    val keyNotNull: Key get() = key ?: getByUid(uid = uid)?.key ?: error(message = "Impossible")

    /**
     * [updateWith] returns another user with app-specific information polled fro [anotherUser].
     */
    fun updateWith(anotherUser: GoogleUser): GoogleUser = copy(
            studentClass = anotherUser.studentClass,
            graduationYear = anotherUser.graduationYear,
            skills = anotherUser.skills,
            introduction = anotherUser.introduction,
            experience = anotherUser.experience,
            freeTimes = anotherUser.freeTimes
    )

    /**
     * Convert the free interval list to string.
     */
    private fun List<FreeTimeInterval>.toStringForm(): String {
        val sb = StringBuilder()
        for (interval in this) {
            sb.append(interval.start)
            sb.append(',')
            sb.append(interval.end)
            sb.append(';')
        }
        if (sb.isNotEmpty()) {
            sb.setLength(sb.length - 1)
        }
        return sb.toString()
    }

    /**
     * [upsert] is used to update the record in the database, either by inserting or updating the
     * existing data.
     *
     * @return the corresponding user with key.
     */
    fun upsert(modifyAppInfo: Boolean = true): GoogleUser {
        val entityOpt = getEntityByUid(uid = uid)
        return UserEntity.upsert(entity = entityOpt) {
            table.uid gets uid
            table.name gets name
            table.email gets email
            table.picture gets picture
            if (entityOpt == null) {
                table.studentClass gets StudentClass.FRESHMAN
                table.graduationYear gets 2022
                table.skills gets "break things"
                table.introduction gets "I write bugs."
                table.experience gets "I broke the codebase in my last intern and got fired."
                table.freeTimes gets ""
            } else if (modifyAppInfo) {
                table.studentClass gets studentClass
                table.graduationYear gets graduationYear
                table.skills gets skills
                table.introduction gets introduction
                table.experience gets experience
                table.freeTimes gets FreeTimeInterval
                        .mergeInterval(freeTimes)
                        .toStringForm()
            }
        }.asGoogleUser
    }

    /**
     * [Table] is the table definition for [GoogleUser]
     */
    private object Table : TypedTable<Table>(tableName = "GoogleUser") {
        // Basic Information
        val uid = stringProperty(name = "uid")
        val name = stringProperty(name = "name")
        val email = stringProperty(name = "email")
        val picture = stringProperty(name = "picture")
        // App Specific Information
        val studentClass = enumProperty(name = "student_class", clazz = StudentClass::class.java)
        val graduationYear = longProperty(name = "graduation_year")
        val skills = longStringProperty(name = "skills")
        val introduction = longStringProperty(name = "introduction")
        val experience = longStringProperty(name = "experience")
        val freeTimes = longStringProperty(name = "free_times")
    }

    /**
     * [UserEntity] is the entity definition for [GoogleUser].
     */
    private class UserEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {

        val uid: String = Table.uid.delegatedValue
        val name: String = Table.name.delegatedValue
        val email: String = Table.email.delegatedValue
        val picture: String = Table.picture.delegatedValue

        val studentClass: StudentClass = Table.studentClass.delegatedValue
        val graduationYear: Long = Table.graduationYear.delegatedValue
        val skills: String = Table.skills.delegatedValue
        val introduction: String = Table.introduction.delegatedValue
        val experience: String = Table.experience.delegatedValue
        val freeTimes: String = Table.freeTimes.delegatedValue

        val asGoogleUser: GoogleUser
            get() = GoogleUser(
                    key = key, uid = uid, name = name, email = email, picture = picture,
                    studentClass = studentClass, graduationYear = graduationYear,
                    skills = skills, introduction = introduction, experience = experience,
                    freeTimes = freeTimes.trim()
                            .takeIf { it.isNotEmpty() }
                            ?.split(";")?.map { pStr ->
                                val p = pStr.split(",")
                                FreeTimeInterval(p[0].toInt(), p[1].toInt())
                            } ?: emptyList()
            )

        companion object : TypedEntityCompanion<Table, UserEntity>(table = Table) {
            override fun create(entity: Entity): UserEntity = UserEntity(entity = entity)
        }
    }

    companion object {

        /**
         * [getEntityByUid] returns a [UserEntity] by [uid], which may be `null`.
         */
        private fun getEntityByUid(uid: String): UserEntity? =
                UserEntity.query { filter { table.uid eq uid } }.firstOrNull()

        /**
         * [getByKey] returns a [GoogleUser] by the given [key], which may be `null`.
         */
        @JvmStatic
        fun getByKey(key: Key): GoogleUser? = UserEntity[key]?.asGoogleUser

        /**
         * [getByUid] returns a [GoogleUser] by [uid], which may be `null`.
         */
        @JvmStatic
        fun getByUid(uid: String): GoogleUser? = getEntityByUid(uid = uid)?.asGoogleUser

        /**
         * [getAllOtherUserKeys] returns a list of all user's keys except [user].
         */
        @JvmStatic
        fun getAllOtherUserKeys(user: GoogleUser): List<Key> =
                UserEntity.allKeys().filter { it != user.keyNotNull }.toList()

        /**
         * [deleteAll] deletes all users.
         */
        @JvmStatic
        fun deleteAll() {
            // To overcome the API limitation of 500 operations
            val keys = UserEntity.allKeys().toList()
            for (key in keys) {
                UserEntity.delete(key)
            }
        }

    }

}
