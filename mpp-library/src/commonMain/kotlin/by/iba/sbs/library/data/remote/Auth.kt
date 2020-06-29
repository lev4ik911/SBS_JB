package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.LoginData
import by.iba.sbs.library.model.response.AuthData
import by.iba.sbs.library.service.LocalSettings
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

@UnstableDefault
@ImplicitReflectionSerializer
class Auth(override val settings: LocalSettings) : Client(settings, false) {
    suspend fun login(loginData: LoginData): Response<AuthData> {

        return post(
            route = Routes.Auth.URL_LOGIN,
            requestBody = loginData
        )
    }
}