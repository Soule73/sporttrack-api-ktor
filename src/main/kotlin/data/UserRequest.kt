package stapi.data

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String? = null,  // Format YYYY-MM-DD
    val gender: String? = null,
    val weight: Double? = null,
    val height: Double? = null
)
