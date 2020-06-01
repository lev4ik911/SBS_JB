package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.StepCreate
import by.iba.sbs.library.model.request.StepEdit
import by.iba.sbs.library.model.response.StepView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
class Steps(override val settings: LocalSettings) : Client(settings) {

    suspend fun getAllSteps(guidelineId: String): Response<List<StepView>> {
        return get(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEPS, guidelineId),
            deserializer = StepView::class.serializer().list,
            needAuth = false
        )
    }

    suspend fun getStep(guidelineId: String, stepId: String): Response<StepView> {
        return get(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEP_DETAILS, guidelineId, stepId),
            needAuth = false
        )
    }

    suspend fun postStep(guidelineId: String, step: StepCreate): Response<StepView> {
        return post(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEPS, guidelineId),
            requestBody = step,
            needAuth = false
        )
    }

    suspend fun putStep(guidelineId: String, stepId: String, step: StepEdit): Response<StepView> {
        return put(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEP_DETAILS, guidelineId, stepId),
            requestBody = step,
            needAuth = false
        )
    }

    suspend fun deleteStep(guidelineId: String, stepId: String): Response<String> {
        return delete(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEP_DETAILS, guidelineId, stepId),
            needAuth = false
        )
    }

}