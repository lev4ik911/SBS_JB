package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class StepView(
    var id: String,
    var name: String,
    var description: String,
    var weight: Int,
    var imagePath: String = "",
    var updateImageTimeSpan: Int = 0
)