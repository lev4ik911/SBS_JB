package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class GuidelineResponse(
    val id: String = "",
    var name: String = "",
    var description: String? = ""
)