package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UserEdit(
    var name: String?,
    var email: String?
)