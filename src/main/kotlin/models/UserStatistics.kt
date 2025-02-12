package stapi.models

import org.jetbrains.exposed.sql.Table

object UserStatistics : Table("user_statistics") {
    val statId = integer("stat_id").autoIncrement()
    val userId = reference("user_id", Users.userId).uniqueIndex()
    val totalDistance = decimal("total_distance", 10, 2).nullable()
    val totalCalories = decimal("total_calories", 10, 2).nullable()
    val totalDuration = integer("total_duration").nullable()
    val favoriteActivity = reference("favorite_activity", ActivityTypes.activityTypeId).nullable()
    val bestPerformance = reference("best_performance", ActivitySessions.sessionId).nullable()

    override val primaryKey = PrimaryKey(statId)
}
