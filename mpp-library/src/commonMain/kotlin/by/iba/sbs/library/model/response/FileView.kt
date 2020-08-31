package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class FileView(
    var id: String = "00000000-0000-0000-0000-000000000000",
    var entities: List<Entity> = listOf(),
    var name: String = "",
    var rating: RatingSummary = RatingSummary(),
    var activity: ActionSummary = ActionSummary()
)