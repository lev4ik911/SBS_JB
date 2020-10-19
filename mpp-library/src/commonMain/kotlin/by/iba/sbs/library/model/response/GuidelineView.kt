package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class GuidelineView(
    val id: String = "",
    var name: String = "",
    var description: String? = "",
    var rating: RatingSummary = RatingSummary(),
    var favourited: Int = 0,
    var activity: ActionSummary = ActionSummary(),
    var preview: FileView? = null
)