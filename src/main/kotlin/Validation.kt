package stapi

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import stapi.data.LoginRequest
import stapi.data.UserRequest
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<UserRequest> { user ->
            val errors = mutableListOf<String>()

            // Validation de l'email
            if (user.email.isBlank()) {
                errors.add("email: L'adresse e-mail ne peut pas être vide.")
            } else if (!Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(user.email)) {
                errors.add("email: Adresse e-mail invalide.")
            }

            // Validation du mot de passe
            if (user.password.length < 8) {
                errors.add("password: Le mot de passe doit contenir au moins 8 caractères.")
            }

            // Validation du prénom
            if (user.firstName.isBlank()) {
                errors.add("firstName: Le prénom ne peut pas être vide.")
            } else if (user.firstName.length > 50) {
                errors.add("firstName: Le prénom ne peut pas dépasser 50 caractères.")
            }

            // Validation du nom de famille
            if (user.lastName.isBlank()) {
                errors.add("lastName: Le nom de famille ne peut pas être vide.")
            } else if (user.lastName.length > 50) {
                errors.add("lastName: Le nom de famille ne peut pas dépasser 50 caractères.")
            }

            // Validation de la date de naissance
            if (user.birthDate != null) {
                try {
                    LocalDate.parse(user.birthDate)
                } catch (e: DateTimeParseException) {
                    errors.add("birthDate: La date de naissance doit être au format YYYY-MM-DD.")
                }
            }

            // Validation du genre
            if (user.gender != null && user.gender !in listOf("M", "F", "O")) {
                errors.add("gender: Le genre doit être 'M', 'F' ou 'O'.")
            }

            // Validation du poids
            if (user.weight != null && user.weight <= 0) {
                errors.add("weight: Le poids doit être un nombre positif.")
            }

            // Validation de la taille
            if (user.height != null && user.height <= 0) {
                errors.add("height: La taille doit être un nombre positif.")
            }

            if (errors.isEmpty()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(errors)
            }
        }

        // Validation pour LoginRequest
        validate<LoginRequest> { login ->
            val errors = mutableListOf<String>()

            // Validation de l'email
            if (login.email.isBlank()) {
                errors.add("email: L'adresse e-mail ne peut pas être vide.")
            } else if (!Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(login.email)) {
                errors.add("email: Adresse e-mail invalide.")
            }

            // Validation du mot de passe
            if (login.password.isBlank()) {
                errors.add("password: Le mot de passe ne peut pas être vide.")
            }

            if (errors.isEmpty()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(errors)
            }
        }
    }
}
