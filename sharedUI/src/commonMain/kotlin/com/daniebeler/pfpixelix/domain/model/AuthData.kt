package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    @SerialName("name") val name: String,
    @SerialName("id") val id: String,
    @SerialName("redirect_uri") val redirectUri: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String
)

@Serializable
data class AuthDataVernissage(
    @SerialName("client_id") val clientId: String,
    @SerialName("redirect_uris") val redirectUris: List<String>
)


@Serializable
data class AuthToken(
    @SerialName("access_token") val accessToken: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class AuthTokenVernissage(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)
