package stapi

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import stapi.routes.authRoutes
import stapi.services.UserService

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/sport_track_db",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = ""
    )

    // Initialisation du service utilisateur avec la base de données
    val userService = UserService(database)

    // Stocker le service utilisateur dans l'attribut de l'application pour y accéder dans les routes
    environment.config.propertyOrNull("ktor.application.userService")?.getString()?.let {
        attributes.put(UserService.Key, userService)
    } ?: run {
        environment.log.info("UserService initialized")
        attributes.put(UserService.Key, userService)
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        authRoutes(userService)

    }
}