package by.iba.sbs.library.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    var name: String = "",
    var email: String = ""
) {
}