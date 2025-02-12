package stapi.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.CurrentDateTime


object UserFriends : Table("user_friend") {
    val userId = reference("user_id", Users.userId)
    val friendId = reference("friend_id", Users.userId)
    val status = varchar("status", 20)
    val requestDate = datetime("request_date").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(userId, friendId)
}