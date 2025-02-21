package stapi

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import stapi.models.ErrorResponse

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.application.environment.log.error("exception /:", cause)

            val errorsList = cause.reasons // Liste des messages d'erreur
            val errorsMap = errorsList.mapNotNull { error ->
                val parts = error.split(":", limit = 2)
                if (parts.size == 2) {
                    val field = parts[0].trim()
                    val message = parts[1].trim()
                    field to message
                } else {
                    null
                }
            }.toMap().ifEmpty { mapOf("error" to "Validation échouée") }
            val code=if(cause.value=="conflict") HttpStatusCode.Conflict else HttpStatusCode.BadRequest
            call.respond(code, ErrorResponse(errors = errorsMap))
        }

        exception<AuthenticationException> { call, cause ->
            call.application.environment.log.error("exception /:", cause)

            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(errors = mapOf("error" to "Authentification échouée")))
        }

        exception<AuthorizationException> { call, cause ->
            call.application.environment.log.error("exception /:", cause)

            call.respond(HttpStatusCode.Forbidden, ErrorResponse(errors = mapOf("error" to "Accès refusé")))
        }

        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Une exception non gérée s'est produite", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(errors = mapOf("error" to "Erreur interne du serveur")))
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()