package by.iba.sbs.library.model.response

import by.iba.sbs.library.model.User

data class AuthData(
    var user: User = User(),
    var accessToken: String = ""
)