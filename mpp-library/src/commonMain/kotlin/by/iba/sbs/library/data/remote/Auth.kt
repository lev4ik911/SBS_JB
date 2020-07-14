package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.LoginData
import by.iba.sbs.library.model.request.RegisterData
import by.iba.sbs.library.model.response.AuthData
import by.iba.sbs.library.model.response.UserView
import by.iba.sbs.library.service.LocalSettings
import by.iba.sbs.library.service.Utils
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

@UnstableDefault
@ImplicitReflectionSerializer
internal class Auth(override val settings: LocalSettings) : Client(settings, false) {
    suspend fun login(loginData: LoginData): Response<AuthData> {
        return post(
            route = Routes.Auth.URL_LOGIN,
            requestBody = loginData
        )
    }

    suspend fun register(registerData: RegisterData): Response<UserView> {
        return post(
            route = Routes.Auth.URL_REGISTER,
            requestBody = registerData
        )
    }

    suspend fun getUserById(userId: String): Response<UserView> {
        return get(
            Utils.formatString(Routes.Users.URL_USER_DETAILS, userId)
        )
    }
}