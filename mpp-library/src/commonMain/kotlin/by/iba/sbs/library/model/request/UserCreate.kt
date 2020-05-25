package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UserCreate(
    var name: String,
    var email: String
)