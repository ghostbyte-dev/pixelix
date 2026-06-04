package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val accountId: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val serverUrl: String,
    val token: String,
    val refreshToken: String,
    val clientId: String,
    val clientSecret: String,
    val createdAt: String
) {
    fun key(): String {
        val cleanUrl =
            serverUrl.removePrefix("https://").removePrefix("http://").removeSuffix("/")
        return "$cleanUrl:$accountId".lowercase()
    }
}