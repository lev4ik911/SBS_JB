package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RatingEdit(
    var rating: Int? = null,
    var comment: String? = null
)