package by.iba.sbs.library.model.response


import kotlinx.serialization.Serializable

@Serializable
data class ActionSummary(
    var createdAt: String = "",
    var createdBy: String = "",
    var updatedAt: String = "",
    var updatedBy: String = ""
)