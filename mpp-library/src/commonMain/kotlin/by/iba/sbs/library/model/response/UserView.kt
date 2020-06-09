package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserView(
    var id: String,
    var name: String,
    var email: String,
    var activity: ActionSummary = ActionSummary()
)