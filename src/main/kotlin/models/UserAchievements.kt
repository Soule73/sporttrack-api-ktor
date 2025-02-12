package stapi.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UserAchievements : Table("user_achievement") {
    val userId = reference("user_id", Users.userId)
    val achievementId = reference("achievement_id", Achievements.achievementId)
    val obtainedDate = datetime("obtained_date").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(userId, achievementId)
}
