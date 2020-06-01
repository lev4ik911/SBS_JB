package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.response.RatingSummary
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
class Ratings(override val settings: LocalSettings) : Client(settings) {
    suspend fun getRatingSummary(guidelineId: String): Response<List<RatingSummary>> {
        return get(
            Utils.formatString(Routes.Ratings.URL_GUIDELINE_RATINGS, guidelineId),
            deserializer = RatingSummary::class.serializer().list,
            needAuth = false
        )
    }
}