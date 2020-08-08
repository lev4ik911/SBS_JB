package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteView(
    var id: String = "00000000-0000-0000-0000-000000000000",
    var entity: Entity = Entity(),
    var activity: ActionSummary = ActionSummary()
)