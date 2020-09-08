package by.iba.sbs.library.model

import by.iba.sbs.library.model.response.RatingSummary
import kotlinx.serialization.Serializable

@Serializable
data class Guideline(
    val id: String = "",
    var name: String = "",
    var descr: String = "",
    var author: String = "Author name",
    var authorId: String = "",
    var isFavorite: Boolean = false,
    var rating: RatingSummary = RatingSummary(),
    var imagePath: String = "",
    var favourited: Int = 0,
    var remoteImageId: String = "",
    var updateImageDateTime: String = ""
//    var steps: MutableList<Step> = mutableListOf(),
//    var feedback: MutableList<Feedback> = mutableListOf()
) {
    val isEmptyPreview get() = (remoteImageId.isEmpty() && updateImageDateTime.isEmpty())
    val isImageNotDownloaded get() = (!isEmptyPreview && imagePath.isEmpty())
    val isImageNotUploaded get() = (updateImageDateTime.isEmpty() && imagePath.isNotEmpty())
}