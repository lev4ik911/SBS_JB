package by.iba.sbs.library.model

import by.iba.sbs.library.model.response.RatingSummary
import kotlinx.serialization.Serializable

@Serializable
data class Guideline(
    val id: String = "",
    var name: String = "",
    var descr: String = "",
    var author: String = "Author name",
    var isFavorite: Boolean = false,
    var rating: RatingSummary = RatingSummary(),
    var imagePath: String = "",
    var updateImageTimeSpan: Int = 0
//    var steps: MutableList<Step> = mutableListOf(),
//    var feedback: MutableList<Feedback> = mutableListOf()
) {
}