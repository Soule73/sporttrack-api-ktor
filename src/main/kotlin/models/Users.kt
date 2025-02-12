package stapi.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.date

object Users : Table("users") {
    val userId = integer("user_id").autoIncrement()
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 255)
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val birthDate = date("birth_date").nullable()
    val gender = char("gender").nullable()
    val weight = decimal("weight", 5, 2).nullable()
    val height = decimal("height", 5, 2).nullable()
    val registrationDate = datetime("registration_date").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(userId)
}
