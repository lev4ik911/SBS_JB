package by.iba.sbs.library.data.remote

import by.iba.sbs.library.model.request.LoginData
import by.iba.sbs.library.model.request.RegisterData
import by.iba.sbs.library.model.response.AuthData
import by.iba.sbs.library.service.LocalSettings
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

    suspend fun register(registerData: RegisterData): Response<AuthData> {
        return post(
            route = Routes.Auth.URL_REGISTER,
            requestBody = registerData
        )
    }

    suspend fun getUserInfo(): Response<AuthData> {
        return get(
            route = Routes.Auth.URL_USER
        )
    }
}