package by.iba.sbs.library.model.response


import kotlinx.serialization.Serializable

@Serializable
data class ActionSummary(
    var createdAt: String = "",
    var createdBy: UserView = UserView(),
    var updatedAt: String = "",
    var updatedBy: UserView = UserView()
)