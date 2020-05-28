package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.UserCreate
import by.iba.sbs.library.model.response.UserView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.serializer

@UnstableDefault
@ImplicitReflectionSerializer
class Users(override val settings: LocalSettings) : Client(settings) {
    suspend fun postUser(user: UserCreate): Response<UserView> {
        return post(
            Routes.Users.URL_USERS,
            requestBody = user,
            needAuth = false
        )
    }

    suspend fun getAllUsers(): Response<List<UserView>> {
        return get(
            Routes.Users.URL_USERS,
            deserializer = UserView::class.serializer().list,
            needAuth = false
        )
    }

    suspend fun getUserById(userId: String): Response<UserView> {
        return get(
            Utils.formatString(Routes.Users.URL_USERS, userId),
            needAuth = false
        )
    }

}