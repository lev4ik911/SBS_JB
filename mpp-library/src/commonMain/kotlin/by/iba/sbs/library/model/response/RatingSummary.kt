package by.iba.sbs.library.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RatingSummary(
    var positive: Int = 0,
    var negative: Int = 0,
    var overall: Int = 0
)