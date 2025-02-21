package stapi.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import stapi.data.LoginRequest
import stapi.data.UserRequest
import stapi.data.UserResponse
import stapi.services.UserService
import java.time.Duration
import java.time.Instant
import java.util.*

@Serializable
data class RegisterResponse(
    val token: String,
    val user: UserResponse
)

fun Route.authRoutes(userService: UserService) {
    route("/auth") {
        post("/register") {
            val userRequest = call.receive<UserRequest>()

            // Vérifie si l'utilisateur existe déjà
            val existingUser = userService.findByEmail(userRequest.email)
            if (existingUser != null) {

                    throw RequestValidationException("conflict",listOf("email: L'adresse e-mail est déjà utilisée."))
//
//                call.respond(HttpStatusCode.Conflict, "Email already in use.")
//                return@post
            }

            // Crée l'utilisateur
            val userId = userService.create(userRequest)
            val userResponse = userService.read(userId)
            // Génère le token JWT
            val token = generateToken(userId, call.application.environment.config)

            if (userResponse != null) {
                // Crée un objet RegisterResponse
                val registerResponse = RegisterResponse(token = token, user = userResponse)
                call.respond(HttpStatusCode.Created, registerResponse)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Échec de la création de l'utilisateur.")
            }

        }

        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val user = userService.verifyPassword(loginRequest.email, loginRequest.password)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid email or password.")
                return@post
            }

            // Génère le token JWT
            val token = generateToken(user.userId, call.application.environment.config)

            val registerResponse = RegisterResponse(token = token, user = user)
            call.respond(registerResponse)
        }

        authenticate("auth-jwt") {
            get("/users/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val user = userService.read(userId)
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Utilisateur non trouvé.")
                }
            }
        }

    }
}

private fun generateToken(userId: Int, config: ApplicationConfig): String {
    val jwtSecret = config.property("jwt.secret").getString()
    val jwtAudience = config.property("jwt.audience").getString()
    val jwtIssuer = config.property("jwt.issuer").getString()

    val expirationDuration = Duration.ofDays(365)
    val expirationDate = Date.from(Instant.now().plus(expirationDuration))

    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim("userId", userId)
        .withExpiresAt(expirationDate)
        .withSubject("Authentication")
        .sign(Algorithm.HMAC256(jwtSecret))
}

