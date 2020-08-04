package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Entity(
    var type: String = "",
    var id: String = "00000000-0000-0000-0000-000000000000"
)