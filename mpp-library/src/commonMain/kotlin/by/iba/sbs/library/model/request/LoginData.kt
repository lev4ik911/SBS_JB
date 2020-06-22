package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    var email: String = "",
    var password: String = ""
)