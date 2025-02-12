package stapi.models

import org.jetbrains.exposed.sql.Table

object ActivityTypes : Table("activity_type") {
    val activityTypeId = integer("activity_type_id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val description = text("description").nullable()

    override val primaryKey = PrimaryKey(activityTypeId)
}
