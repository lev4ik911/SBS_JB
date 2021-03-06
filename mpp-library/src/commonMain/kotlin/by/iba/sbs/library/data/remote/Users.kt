package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.UserCreate
import by.iba.sbs.library.model.response.AuthData
import by.iba.sbs.library.model.response.FavoriteView
import by.iba.sbs.library.model.response.GuidelineView
import by.iba.sbs.library.model.response.UserView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
internal class Users(override val settings: LocalSettings) : Client(settings) {
    suspend fun createUser(user: UserCreate): Response<UserView> {
        return post(
            Routes.Users.URL_USERS,
            requestBody = user
        )
    }

    suspend fun updateUser(user: UserCreate): Response<UserView> {
        return put(
            Routes.Users.URL_USERS_MY,
            requestBody = user
        )
    }

    suspend fun getUserInfo(): Response<AuthData> {
        return get(
            route = Routes.Auth.URL_USER
        )
    }

    suspend fun getAllUsers(): Response<List<UserView>> {
        return get(
            Routes.Users.URL_USERS,
            deserializer = UserView::class.serializer().list
        )
    }

    suspend fun getUserById(userId: String): Response<UserView> {
        return get(
            Utils.formatString(Routes.Users.URL_USER_DETAILS, userId)
        )
    }

    suspend fun getUserGuidelines(userId: String): Response<List<GuidelineView>> {
        return get(
            Utils.formatString(Routes.Users.URL_USER_GUIDELINES, userId),
            deserializer = GuidelineView::class.serializer().list
        )
    }

    suspend fun getUserFavorites(userId: String): Response<List<FavoriteView>> {
        return get(
            Utils.formatString(Routes.Users.URL_USER_FAVORITES, userId),
            deserializer = FavoriteView::class.serializer().list
        )
    }

    suspend fun deleteUser(userId: String): Response<UserView> {
        return post(
            Routes.Users.URL_USERS,
            requestBody = userId
        )
    }
}