package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    var user: UserView = UserView(),
    var accessToken: String = ""
)