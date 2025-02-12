package stapi.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ActivitySessions : Table("activity_session") {
    val sessionId = integer("session_id").autoIncrement()
    val userId = reference("user_id", Users.userId)
    val activityTypeId = reference("activity_type_id", ActivityTypes.activityTypeId)
    val startDate = datetime("start_date")
    val endDate = datetime("end_date").nullable()
    val duration = integer("duration").nullable() // En secondes
    val distance = decimal("distance", 6, 2).nullable()
    val caloriesBurned = decimal("calories_burned", 6, 2).nullable()
    val averagePace = decimal("average_pace", 5, 2).nullable()
    val comment = text("comment").nullable()
    val status = varchar("status", 20).default("private")

    override val primaryKey = PrimaryKey(sessionId)
}
