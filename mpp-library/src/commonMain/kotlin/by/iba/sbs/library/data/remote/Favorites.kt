package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.response.FavoriteView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

@UnstableDefault
@ImplicitReflectionSerializer
internal class Favorites(override val settings: LocalSettings) : Client(settings) {

    suspend fun postFavorite(guidelineId: String): Response<FavoriteView> {
        return post(
            Utils.formatString(Routes.Favorites.URL_GUIDELINE_FAVORITES, guidelineId),
            requestBody = ""
        )
    }

    suspend fun deleteFavorite(guidelineId: String, favoriteId: String): Response<String> {
        return delete(
            Utils.formatString(
                Routes.Favorites.URL_GUIDELINE_FAVORITES_DETAILS,
                guidelineId,
                favoriteId
            )
        )
    }
}