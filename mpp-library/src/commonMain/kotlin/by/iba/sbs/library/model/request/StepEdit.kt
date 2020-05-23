package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class StepEdit(
    var name: String?,
    var description: String?,
    var weight: Int?
)