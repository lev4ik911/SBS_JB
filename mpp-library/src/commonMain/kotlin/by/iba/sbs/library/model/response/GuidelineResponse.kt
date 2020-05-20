package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
class GuidelineResponse(
    val id: String = "",
    var name: String = "",
    var description: String = ""
) {
}