package by.iba.sbs.library.repository

import by.iba.sbs.library.data.remote.Auth
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.request.LoginData
import by.iba.sbs.library.model.response.AuthData
import by.iba.sbs.library.service.LocalSettings
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IAuthRepository {
    suspend fun login(loginData: LoginData): Response<AuthData>
}

class AuthRepository @UnstableDefault constructor(val settings: LocalSettings) :
    IAuthRepository {
    @ImplicitReflectionSerializer
    @UnstableDefault
    private val auth = Auth(settings)

    @UnstableDefault
    @ImplicitReflectionSerializer
    override suspend fun login(loginData: LoginData): Response<AuthData> {
        return auth.login(loginData)
    }

}