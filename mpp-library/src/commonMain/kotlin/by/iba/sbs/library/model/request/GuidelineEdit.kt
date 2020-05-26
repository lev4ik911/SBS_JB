package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GuidelineEdit (
    var name: String? = "",
    var description: String? = ""
)