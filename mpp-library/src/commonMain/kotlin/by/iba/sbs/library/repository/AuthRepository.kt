package by.iba.sbs.library.repository

import by.iba.sbs.library.data.remote.Auth
import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.request.LoginData
import by.iba.sbs.library.model.request.RegisterData
import by.iba.sbs.library.model.response.AuthData
import by.iba.sbs.library.model.response.UserView
import by.iba.sbs.library.service.LocalSettings
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault

interface IAuthRepository {
    suspend fun login(loginData: LoginData): Response<AuthData>
    suspend fun register(registerData: RegisterData): Response<UserView>
}

class AuthRepository @UnstableDefault constructor(val settings: LocalSettings) :
    IAuthRepository {
    @ImplicitReflectionSerializer
    @UnstableDefault
    private val auth by lazy { Auth(settings) }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override suspend fun login(loginData: LoginData): Response<AuthData> {
        return auth.login(loginData)
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override suspend fun register(registerData: RegisterData): Response<UserView> {
        return auth.register(registerData)
    }
}