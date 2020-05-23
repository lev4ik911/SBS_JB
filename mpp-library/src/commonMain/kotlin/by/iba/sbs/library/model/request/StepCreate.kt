package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class StepCreate(
    var name: String,
    var description: String,
    var weight: Int?
)