package by.iba.sbs.library.model

import kotlinx.serialization.Serializable

@Serializable
class Guideline(
    val id: String = "",
    var name: String = "",
    var description: String = "",
    var author: String = "",
    var isFavorite: Boolean = false
//    var steps: MutableList<Step> = mutableListOf(),
//    var feedback: MutableList<Feedback> = mutableListOf()
) {
}