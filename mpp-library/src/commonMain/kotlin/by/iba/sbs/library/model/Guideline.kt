package by.iba.sbs.library.model

import by.iba.sbs.library.model.response.RatingSummary
import kotlinx.serialization.Serializable

@Serializable
data class Guideline(
    val id: String = "",
    var name: String = "",
    var description: String = "",
    var author: String = "",
    var isFavorite: Boolean = false,
    var rating: RatingSummary = RatingSummary()
//    var steps: MutableList<Step> = mutableListOf(),
//    var feedback: MutableList<Feedback> = mutableListOf()
) {
}