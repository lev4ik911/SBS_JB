package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GuidelineCreate(
    var name: String = "",
    var description: String? = ""
)