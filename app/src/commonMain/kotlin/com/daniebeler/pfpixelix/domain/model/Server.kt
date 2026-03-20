package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Server(
    @SerialName("thumbnail") val thumbnail: String?,
    @SerialName("domain") val domain: String,
    @SerialName("title") val title: String?,
    @SerialName("description") val description: String?,
    @SerialName("openRegistration") val openRegistration: Boolean?,
    @SerialName("version") val version: String?,
    @SerialName("totalUsers") val totalUsers: Int?,
    @SerialName("activeUsersMonth") val activeUsersMonth: Int?,
    @SerialName("activeUsersHalfYear") val activeUsersHalfYear: Int?,
    @SerialName("localPosts") val localPosts: Int?
)