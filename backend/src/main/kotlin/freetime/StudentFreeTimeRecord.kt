package freetime

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [StudentFreeTimeRecord] is the record for student's free time.
 */
data class StudentFreeTimeRecord(
        val key: Key? = null, val studentId: Key, val record: List<FreeTimeInterval>
) {

    private object Table : TypedTable<Table>(tableName = "StudentFreeTimeRecord") {
        val studentId = keyProperty(name = "student_id")
        val record = longStringProperty(name = "record")
    }

    private class RecordEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val studentId: Key = Table.studentId.delegatedValue
        val record: String = Table.record.delegatedValue

        val asStudentFreeTimeRecord: StudentFreeTimeRecord
            get() {
                val recordList = record.trim()
                        .takeIf { it.isNotEmpty() }
                        ?.split(";")?.map { pStr ->
                            val p = pStr.split(",")
                            FreeTimeInterval(p[0].toInt(), p[1].toInt())
                        } ?: emptyList()
                return StudentFreeTimeRecord(
                        key = key, studentId = studentId, record = recordList
                )
            }

        companion object : TypedEntityCompanion<Table, RecordEntity>(table = Table) {

            override fun create(entity: Entity): RecordEntity = RecordEntity(entity = entity)

        }
    }

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
     * [upsert] upserts the record into the database.
     */
    fun upsert(): StudentFreeTimeRecord {
        val entityOpt = key?.let { RecordEntity[it] }
        return RecordEntity.upsert(entity = entityOpt) {
            table.studentId gets studentId
            table.record gets FreeTimeInterval.mergeInterval(record).toStringForm()
        }.asStudentFreeTimeRecord
    }

    companion object {

        /**
         * [getByStudentId] returns the [StudentFreeTimeRecord] of a student with [studentId].
         */
        fun getByStudentId(studentId: Key): StudentFreeTimeRecord {
            val entityOpt = RecordEntity
                    .query { filter { table.studentId eq studentId } }
                    .firstOrNull()
            if (entityOpt != null) {
                return entityOpt.asStudentFreeTimeRecord
            }
            return RecordEntity.insert {
                table.studentId gets studentId
                table.record gets ""
            }.asStudentFreeTimeRecord
        }

    }

}
