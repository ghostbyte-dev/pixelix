package com.daniebeler.pfpixelix.domain.repository.vernissage

import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.AuthDataVernissage
import com.daniebeler.pfpixelix.domain.model.AuthToken
import com.daniebeler.pfpixelix.domain.model.AuthTokenVernissage
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageAccountDto
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


interface VernissageAuthApi {
    companion object {
        fun createVernissageAuthApi(baseUrl: Url, json: Json): VernissageAuthApi {
            val httpClient = HttpClient {
                install(ContentNegotiation) { json(json) }
                install(Logging) {
                    logger = object : io.ktor.client.plugins.logging.Logger {
                        override fun log(message: String) {
                            Logger.v(tag = "Vernissage HttpAuth") {
                                message.lines().joinToString { "\n\t\t$it" }
                            }
                        }
                    }
                    level = LogLevel.INFO
                }
            }
            val ktorfit = Ktorfit.Builder()
                .httpClient(httpClient)
                .baseUrl(baseUrl.toString())
                .build()
            return ktorfit.createVernissageAuthApi()
        }
    }

    @FormUrlEncoded
    @POST("api/v1/auth-dynamic-clients")
    suspend fun getAuthData(
        @Field("client_name") clientName: String,
        @Field("redirect_uris") redirectUris: List<String>,
        @Field("grant_types") grantTypes: List<String> = listOf("authorization_code", "refresh_token"),
        @Field("response_types") responseTypes: List<String> = listOf("code")
    ): AuthDataVernissage

    @FormUrlEncoded
    @POST("api/v1/oauth/token")
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String?,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String
    ): AuthTokenVernissage

    @FormUrlEncoded
    @POST("api/v1/oauth/token")
    suspend fun getTokenRefresh(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String
    ): AuthTokenVernissage

    @GET("api/v1/users/{username}")
    suspend fun verify(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): VernissageAccountDto
}