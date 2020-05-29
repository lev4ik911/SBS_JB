package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.response.StepView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
class Ratings(override val settings: LocalSettings) : Client(settings) {
    suspend fun getAllRatings(guidelineId: String): Response<List<StepView>> {
        return get(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEPS, guidelineId),
            deserializer = StepView::class.serializer().list,
            needAuth = false
        )
    }
}