package stapi

import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {

    io.ktor.server.netty.EngineMain.main(args)

}

fun loadEnvVariables(): Dotenv {
    return Dotenv.configure()
        .directory(".")
        .load()
}

@Suppress("unused")
fun Application.module() {
    val dotenv = loadEnvVariables()

    val dbUrl = dotenv["DATABASE_URL"]
    val dbUser = dotenv["DATABASE_USER"]
    val dbPassword = dotenv["DATABASE_PASSWORD"]

    if (dbUrl == null || dbUser == null || dbPassword == null) {
        throw IllegalArgumentException("Database configuration values cannot be null")
    }

    val database = Database.connect(
        url = dbUrl,
        driver = "com.mysql.cj.jdbc.Driver",
        user = dbUser,
        password = dbPassword
    )

    // Stocker la base de données dans un singleton pour qu'elle soit réutilisable partout
    DatabaseSingleton.database = database

    configureSerialization()
    configureStatusPages()
    configureSecurity()
    configureValidation()
    configureDatabases()
    configureHTTP()
    configureMonitoring()

    //Décomments pour migrer la base de données
    //configureMigrations()

    // configureSockets()
}

object DatabaseSingleton {
    lateinit var database: Database
}