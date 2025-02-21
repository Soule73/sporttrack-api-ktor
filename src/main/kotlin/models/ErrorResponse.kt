package stapi.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val errors: Map<String, String>
)
