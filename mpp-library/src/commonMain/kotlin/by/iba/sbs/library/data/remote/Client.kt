package by.iba.sbs.library.data.remote

import by.iba.sbs.library.service.LocalSettings
import com.github.aakira.napier.Napier
import dev.icerock.moko.network.exceptionfactory.HttpExceptionFactory
import dev.icerock.moko.network.exceptionfactory.parser.ErrorExceptionParser
import dev.icerock.moko.network.exceptionfactory.parser.ValidationExceptionParser
import dev.icerock.moko.network.features.ExceptionFeature
import dev.icerock.moko.network.features.TokenFeature
import io.ktor.client.HttpClient
import io.ktor.client.features.ResponseException
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer


@UnstableDefault
@ImplicitReflectionSerializer
open class Client(open val settings: LocalSettings) {

    private val json: Json by lazy {
        Json(JsonConfiguration.Default)
    }

    private val ktor: HttpClient by lazy {
        HttpClient {
            defaultRequest {
                headers {
                    append("Accept-Language", "en-US")
                    append("Accept", "application/json")
                }
            }
            install(ExceptionFeature) {
                exceptionFactory = HttpExceptionFactory(
                    defaultParser = ErrorExceptionParser(json),
                    customParsers = mapOf(
                        HttpStatusCode.UnprocessableEntity.value to ValidationExceptionParser(json)
                    )
                )
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(message = message)
                    }
                }
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }
            install(TokenFeature) {
                tokenHeaderName = "Authorization"
                tokenProvider = object : TokenFeature.TokenProvider {
                    override fun getToken(): String? = settings.accessToken
                }
            }

            // disable standard BadResponseStatus - exceptionfactory do it for us
            expectSuccess = false
        }
    }

    private suspend inline fun <reified T : Any> HttpResponse.processResponse(deserializer: DeserializationStrategy<T>? = null): Response<T> {
        return this.run {
            try {
                when (this.status) {
                    HttpStatusCode.OK, HttpStatusCode.Created -> {
                        val s = this.readText()
                        println(s)
                        val res = if (deserializer == null)
                            json.parse(T::class.serializer(), s)
                        else
                            json.parse(deserializer, s)
                        Response.success(res)
                    }
                    HttpStatusCode.NoContent -> {
                        Response.success(null)
                    }
                    HttpStatusCode.Unauthorized -> {
                        Response.unauthorized()
                    }
                    else -> {
                        println(this.status)
                        val s = this.readText()
                        println(s)
                        //val errorInfo = Json.parse(ErrorInfo::class.serializer(), s)
                        Response.error(Throwable(s), null)
                    }
                }
            } catch (e: ResponseException) {
                Response.error(e, null)
            }
        }
    }

    internal suspend inline fun <reified T : Any> get(
        route: String,
        query: Map<String, String> = mutableMapOf(),
        needAuth: Boolean = true,
        deserializer: DeserializationStrategy<T>? = null
    ): Response<T> {

        return try {
            ktor
                .request<HttpResponse>(route) {
                    if (needAuth) {
                        header(HttpHeaders.Authorization, "Bearer ${settings.accessToken}")
                    }
                    method = HttpMethod.Get
                    query.forEach {
                        parameter(it.key, it.value)
                    }
                }
                .processResponse(deserializer)

        } catch (ex: Exception) {
            Response.error(ex, null)
        }
    }

    internal suspend inline fun <reified T : Any> post(
        route: String,
        requestBody: Any,
        query: Map<String, String> = mutableMapOf(),
        needAuth: Boolean = true,
        deserializer: DeserializationStrategy<T>? = null
    ): Response<T> {
        return try {
            ktor
                .request<HttpResponse>(route) {
                    if (needAuth) {
                        header(HttpHeaders.Authorization, "Bearer ${settings.accessToken}")
                    }
                    contentType(ContentType.Application.Json)
                    method = HttpMethod.Post
                    body = requestBody
                    query.forEach {
                        parameter(it.key, it.value)
                    }
                }
                .processResponse(deserializer)

        } catch (ex: Exception) {
            Response.error(ex, null)
        }
    }

    internal suspend inline fun <reified T : Any> put(
        route: String,
        requestBody: Any,
        query: Map<String, String> = mutableMapOf(),
        needAuth: Boolean = true,
        deserializer: DeserializationStrategy<T>? = null
    ): Response<T> {

        return try {
            ktor
                .request<HttpResponse>(route) {
                    if (needAuth) {
                        header(HttpHeaders.Authorization, "Bearer ${settings.accessToken}")
                    }
                    contentType(ContentType.Application.Json)
                    method = HttpMethod.Put
                    body = requestBody
                    query.forEach {
                        parameter(it.key, it.value)
                    }
                }
                .processResponse(deserializer)

        } catch (ex: Exception) {
            Response.error(ex, null)
        }
    }

    internal suspend inline fun <reified T : Any> delete(
        route: String,
        needAuth: Boolean = true
    ): Response<T> {
        return try {
            ktor
                .request<HttpResponse>(route) {

                    if (needAuth) {
                        header(HttpHeaders.Authorization, "Bearer ${settings.accessToken}")
                    }
                    method = HttpMethod.Delete
                }
                .processResponse()

        } catch (ex: Exception) {
            Response.error(ex, null)
        }
    }
}