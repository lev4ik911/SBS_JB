package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.GuidelineCreate
import by.iba.sbs.library.model.request.GuidelineEdit
import by.iba.sbs.library.model.response.FileView
import by.iba.sbs.library.model.response.GuidelineView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import io.ktor.http.HttpMethod
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
internal class Guidelines(override val settings: LocalSettings) : Client(settings) {
    suspend fun getAllGuidelines(): Response<List<GuidelineView>> {
        return get(
            Routes.Guidelines.URL_GUIDELINES,
            deserializer = GuidelineView::class.serializer().list
        )
    }

    suspend fun getPopularGuidelines(): Response<List<GuidelineView>> {
        return get(
            Routes.Guidelines.URL_GUIDELINES_POPULAR,
            deserializer = GuidelineView::class.serializer().list
        )
    }

    suspend fun getGuideline(id: String): Response<GuidelineView> {
        return get(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_DETAILS, id)
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

    suspend fun postGuidelineImage(guidelineId: String, data: ByteArray): Response<FileView> {
        return uploadFile(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_FILES, guidelineId),
            HttpMethod.Post,
            data
        )
    }

    suspend fun putGuidelineImage(guidelineId: String, imageId: String, data: ByteArray): Response<FileView> {
        return uploadFile(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_FILE_DETAILS, guidelineId, imageId),
            HttpMethod.Put,
            data
        )
    }

    suspend fun postStepImage(guidelineId: String, stepId: String, data: ByteArray): Response<FileView> {
        return uploadFile(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEP_FILES, guidelineId, stepId),
            HttpMethod.Post,
            data
        )
    }

    suspend fun putStepImage(guidelineId: String, stepId: String, imageId: String, data: ByteArray): Response<FileView> {
        return uploadFile(
            Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEP_FILE_DETAILS, guidelineId, stepId, imageId),
            HttpMethod.Put,
            data
        )
    }

    fun generateURLForGuidelineImage(guidelineId: String, imageId: String):String {
        return Utils.formatString(Routes.Guidelines.URL_GUIDELINE_FILE_DETAILS, guidelineId, imageId)
    }

    fun generateURLForStepImage(guidelineId: String, stepId:String, imageId: String):String {
        return Utils.formatString(Routes.Guidelines.URL_GUIDELINE_STEP_FILE_DETAILS, guidelineId, stepId, imageId)
    }

}