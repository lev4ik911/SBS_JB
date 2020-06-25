package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.RatingCreate
import by.iba.sbs.library.model.request.RatingEdit
import by.iba.sbs.library.model.response.RatingView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
class Feedback(override val settings: LocalSettings) : Client(settings) {
    suspend fun getAllFeedbacks(guidelineId: String): Response<List<RatingView>> {
        return get(
            Utils.formatString(Routes.Ratings.URL_GUIDELINE_RATINGS, guidelineId),
            deserializer = RatingView::class.serializer().list
        )
    }

    suspend fun getFeedback(guidelineId: String, feedbackId: String): Response<RatingView> {
        return get(
            Utils.formatString(
                Routes.Ratings.URL_GUIDELINE_RATING_DETAILS,
                guidelineId,
                feedbackId
            )
        )
    }

    suspend fun postFeedback(guidelineId: String, rating: RatingCreate): Response<RatingView> {
        return post(
            Utils.formatString(Routes.Ratings.URL_GUIDELINE_RATINGS, guidelineId),
            requestBody = rating
        )
    }

    suspend fun putFeedback(
        guidelineId: String,
        feedbackId: String,
        rating: RatingEdit
    ): Response<RatingView> {
        return put(
            Utils.formatString(
                Routes.Ratings.URL_GUIDELINE_RATING_DETAILS,
                guidelineId,
                feedbackId
            ),
            requestBody = rating
        )
    }

    suspend fun deleteFeedback(guidelineId: String, feedbackId: String): Response<String> {
        return delete(
            Utils.formatString(
                Routes.Ratings.URL_GUIDELINE_RATING_DETAILS,
                guidelineId,
                feedbackId
            )
        )
    }
}