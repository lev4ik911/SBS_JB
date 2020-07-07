package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterData(
    var name: String = "",
    var email: String = "",
    var password: String = ""
)