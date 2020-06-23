package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.GuidelineCreate
import by.iba.sbs.library.model.request.GuidelineEdit
import by.iba.sbs.library.model.response.GuidelineView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
class Guidelines(override val settings: LocalSettings) : Client(settings) {
    suspend fun getAllGuidelines(): Response<List<GuidelineView>> {
        return get(
            Routes.Guidelines.URL_GUIDELINES,
            deserializer = GuidelineView::class.serializer().list,
            needAuth = false
        )
    }

    suspend fun getGuideline(id: String): Response<GuidelineView> {
        return get(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_DETAILS, id),
            needAuth = false
        )
    }


    suspend fun postGuideline(guideline: GuidelineCreate): Response<GuidelineView> {
        return post(
            Routes.Guidelines.URL_GUIDELINES,
            requestBody = guideline
        )
    }

    suspend fun putGuideline(guidelineId: String, guideline: GuidelineEdit): Response<GuidelineView> {
        return put(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_DETAILS, guidelineId),
            requestBody = guideline
        )
    }
    suspend fun deleteGuideline(guidelineId: String): Response<String> {
        return delete(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_DETAILS, guidelineId)
        )
    }

}