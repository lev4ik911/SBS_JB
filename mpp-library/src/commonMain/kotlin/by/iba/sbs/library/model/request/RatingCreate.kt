package by.iba.sbs.library.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RatingCreate(
    var rating: Int = 0,
    var comment: String? = null
)