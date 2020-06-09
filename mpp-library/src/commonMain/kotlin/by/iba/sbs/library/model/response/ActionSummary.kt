package by.iba.sbs.library.model.response


import kotlinx.serialization.Serializable

@Serializable
data class ActionSummary(
    var createdAt: String = "",
    var updatedAt: String = ""
)