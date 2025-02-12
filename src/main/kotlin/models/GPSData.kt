package stapi.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object GPSData : Table("gps_data") {
    val gpsId = integer("gps_id").autoIncrement()
    val sessionId = reference("session_id", ActivitySessions.sessionId)
    val timestamp = datetime("timestamp")
    val latitude = decimal("latitude", 9, 6)
    val longitude = decimal("longitude", 9, 6)
    val altitude = decimal("altitude", 6, 2).nullable()
    val speed = decimal("speed", 5, 2).nullable()
    val pace = decimal("pace", 5, 2).nullable()
    val heartRate = integer("heart_rate").nullable()

    override val primaryKey = PrimaryKey(gpsId)
}
