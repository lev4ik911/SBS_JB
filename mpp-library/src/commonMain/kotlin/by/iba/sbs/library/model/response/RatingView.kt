package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RatingView(
    var id: String = "00000000-0000-0000-0000-000000000000",
    var entity: Entity = Entity(),
    var rating: Int = 0,
    var comment: String? = null,
    var activity: ActionSummary = ActionSummary()
)