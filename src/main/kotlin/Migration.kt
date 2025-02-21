package stapi

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import stapi.models.*
import java.time.LocalDate
import java.time.LocalDateTime

fun configureMigrations() {

    val database = DatabaseSingleton.database

    fun initDatabase(database: Database) {
        transaction(database) {
            SchemaUtils.create(
                Users,
                ActivityTypes,
                ActivitySessions,
                GPSData,
                Achievements,
                UserStatistics,
                UserAchievements,
                UserFriends // Si tu implémentes cette fonctionnalité
            )
        }
    }

    //initDatabase(database)

    transaction(database) {
        if (Users.selectAll().where { Users.email eq "john.doe@example.com" }.empty()) {
            Users.insert {
                it[email] = "john.doe@example.com"
                it[password] = hashPassword("monSuperMotDePasse123")
                it[firstName] = "John"
                it[lastName] = "Doe"
                it[birthDate] = LocalDate.parse("1990-05-15")
                it[gender] = 'M'
                it[weight] = 75.5.toBigDecimal()
                it[height] = 180.0.toBigDecimal()
            }
            // Insère d'autres utilisateurs si nécessaire
        }
    }
    transaction(database) {
        ActivityTypes.batchInsert(
            listOf(
                "Running" to "Activités de course à pied.",
                "Cycling" to "Activités de cyclisme sur route ou VTT.",
                "Swimming" to "Activités de natation en piscine ou en eau libre.",
                "Fitness" to "Activités de fitness en salle."
            )
        ) { activity ->
            this[ActivityTypes.name] = activity.first
            this[ActivityTypes.description] = activity.second
        }
    }

    transaction(database) {
        val userId = Users.selectAll()
            .where { Users.email eq "john.doe@example.com" }
            .singleOrNull()?.get(Users.userId) ?: return@transaction

        val activityTypeId = ActivityTypes.selectAll()
            .where { ActivityTypes.name eq "Running" }
            .singleOrNull()?.get(ActivityTypes.activityTypeId) ?: return@transaction

        ActivitySessions.insert {
            it[this.userId] = userId
            it[this.activityTypeId] = activityTypeId
            it[startDate] = LocalDateTime.parse("2023-10-29T07:00:00")
            it[endDate] = LocalDateTime.parse("2023-10-29T07:45:00")
            it[duration] = 2700
            it[distance] = 10.0.toBigDecimal()
            it[caloriesBurned] = 600.toBigDecimal()
            it[averagePace] = 4.5.toBigDecimal()
            it[comment] = "Morning run around the park"
            it[status] = "private"
        }
        // Insère d'autres sessions si nécessaire
    }


}

// Fonction de hashage du mot de passe
fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}
