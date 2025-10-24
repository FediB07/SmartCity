package tn.esprit.smartcity.Utilisateurs.model

import java.util.UUID

/**
 * User data class representing a user in the Smart City application
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val email: String
)
