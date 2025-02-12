package stapi.services

import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.mindrot.jbcrypt.BCrypt
import stapi.data.UserRequest
import stapi.data.UserResponse
import stapi.models.Users
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Mark the class as open to allow inheritance
open class UserService(private val database: Database) {

    companion object {
        val Key = AttributeKey<UserService>("UserService")
    }

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db = database) { block() }

    // Création d'un utilisateur
    suspend fun create(userRequest: UserRequest): Int = dbQuery {
        Users.insert {
            it[email] = userRequest.email
            it[password] = hashPassword(userRequest.password)
            it[firstName] = userRequest.firstName
            it[lastName] = userRequest.lastName
            it[birthDate] = userRequest.birthDate?.let { date -> LocalDate.parse(date, dateFormatter) }
            it[gender] = userRequest.gender?.firstOrNull()
            it[weight] = userRequest.weight?.toBigDecimal()
            it[height] = userRequest.height?.toBigDecimal()
        }[Users.userId]
    }

    // Trouver un utilisateur par email
    suspend fun findByEmail(email: String): UserResponse? = dbQuery {
        Users.selectAll().where { Users.email eq email }
            .map { rowToUserResponse(it) }
            .singleOrNull()
    }

    // Lecture d'un utilisateur par ID
    suspend fun read(id: Int): UserResponse? = dbQuery {
        Users.selectAll().where { Users.userId eq id }
            .map { rowToUserResponse(it) }
            .singleOrNull()
    }

    // Vérifier le mot de passe
    suspend fun verifyPassword(email: String, password: String): UserResponse? = dbQuery {
        val user = Users.selectAll().where { Users.email eq email }
            .map { it }
            .singleOrNull()
        if (user != null && BCrypt.checkpw(password, user[Users.password])) {
            rowToUserResponse(user)
        } else {
            null
        }
    }

    // Fonction pour convertir une ligne de base de données en UserResponse
    private fun rowToUserResponse(row: ResultRow): UserResponse {
        return UserResponse(
            userId = row[Users.userId],
            email = row[Users.email],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            birthDate = row[Users.birthDate]?.format(dateFormatter),
            gender = row[Users.gender]?.toString(),
            weight = row[Users.weight]?.toDouble(),
            height = row[Users.height]?.toDouble(),
            registrationDate = row[Users.registrationDate].format(dateTimeFormatter)
        )
    }

    // Hashage du mot de passe
    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

}