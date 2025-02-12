package stapi.models

import org.jetbrains.exposed.sql.Table

object Achievements : Table("achievement") {
    val achievementId = integer("achievement_id").autoIncrement()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val criteria = text("criteria") // JSON stocké sous forme de texte

    override val primaryKey = PrimaryKey(achievementId)
}
