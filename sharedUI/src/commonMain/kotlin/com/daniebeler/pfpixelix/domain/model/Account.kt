package com.daniebeler.pfpixelix.domain.model

import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import com.daniebeler.pfpixelix.domain.service.session.Credentials
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("id") val id: String = "",
    @SerialName("username") val username: String = "",
    @SerialName("acct") val acct: String = "",
    @SerialName("display_name") val displayname: String? = null,
    @SerialName("avatar") val avatar: String = "",
    @SerialName("followers_count")val followersCount: Int = 0,
    @SerialName("following_count")val followingCount: Int = 0,
    @SerialName("statuses_count") val postsCount: Int = 0,
    @SerialName("website") val website: String = "",
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("note") val note: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("locked") val locked: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("is_admin") val isAdmin: Boolean = false,
    @SerialName("pronouns") val pronouns: List<String> = emptyList()
)

fun credentialsToAccount(credentials: Credentials) = Account(
    username = credentials.username,
    avatar = credentials.avatar,
    url = credentials.serverUrl,
    id = credentials.accountId,
    displayname = credentials.displayName,
    followersCount = 0,
    acct = "",
    note = "",
    locked = false,
    isAdmin = false,
    createdAt = "",
    postsCount = 0,
    followingCount = 0,
    website = "",
    pronouns = emptyList()
)
