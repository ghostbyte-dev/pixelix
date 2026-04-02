package com.daniebeler.pfpixelix.domain.service.session

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Server
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface AuthApi {
    @POST("api/v1/apps")
    suspend fun getAuthData(
        @Query("client_name") clientName: String,
        @Query("redirect_uris") redirectUrl: String
    ): AuthData

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String
    ): AuthToken

    @GET("api/v1/accounts/verify_credentials")
    suspend fun verify(
        @Header("Authorization") token: String
    ): Account
}

@Serializable
data class AuthData(
    @SerialName("name") val name: String,
    @SerialName("id") val id: String,
    @SerialName("redirect_uri") val redirectUri: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String
)

@Serializable
data class AuthToken(
    @SerialName("access_token") val accessToken: String,
    @SerialName("created_at") val createdAt: String
)