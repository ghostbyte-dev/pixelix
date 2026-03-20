package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.fediverse_logo
import pixelix.app.generated.resources.lemmy_logo
import pixelix.app.generated.resources.mastodon_logo
import pixelix.app.generated.resources.misskey_logo
import pixelix.app.generated.resources.peertube_logo
import pixelix.app.generated.resources.pixelfed_logo

@Serializable
data class NodeInfo(
    @SerialName("software") val software: String,
    @SerialName("usage") val usage: NodeinfoUsage,
    @SerialName("metadata") val metadata: NodeinfoMetadata
)

@Serializable
data class NodeinfoMetadata(
    @SerialName("nodeDescription") val nodeDescription: String = "",
    @SerialName("nodeName") val nodeName: String = ""
)

@Serializable
data class NodeinfoUsage(
    @SerialName("localComments") val localComments: Int = 0,
    @SerialName("localPosts") val localPosts: Int = 0,
    @SerialName("users") val users: NodeinfoUsers = NodeinfoUsers()
)

@Serializable
data class NodeinfoUsers(
    @SerialName("activeHalfyear") val activeHalfyear: Int = 0,
    @SerialName("activeMonth") val activeMonth: Int = 0,
    @SerialName("total") val total: Int = 0
)

@Serializable
data class ServerLocation(
    @SerialName("city") val city: String?,
    @SerialName("country") val country: String?,
)

@Serializable
data class ServerStats(
    @SerialName("monthly_active_users") val monthlyActiveUsers: Int,
    @SerialName("status_count") val statusCount: Int,
    @SerialName("user_count") val userCount: Int
)

@Serializable
data class SoftwareSmall(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("version") val version: String
)