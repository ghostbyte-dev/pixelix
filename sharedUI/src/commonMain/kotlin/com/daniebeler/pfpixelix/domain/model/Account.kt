package com.daniebeler.pfpixelix.domain.model

data class Account(
    override val id: String = "",
    val username: String = "",
    val acct: String = "",
    val displayname: String? = null,
    val avatar: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val website: String = "",
    val note: String = "",
    val url: String = "",
    val locked: Boolean = false,
    val createdAt: String = "",
    val isAdmin: Boolean = false,
    val pronouns: List<String> = emptyList(),
    val headerUrl: String? = null,
    val fields: List<Field> = emptyList(),
    val isSupporterFlagEnabled: Boolean = false,
    val manuallyApprovesFollowers: Boolean? = null,
    val includeProfilePageInSearchEngines: Boolean? = null,
    val includePublicPostsInSearchEngines: Boolean? = null
): Identifiable {
    companion object {
        fun unknown() = Account(
            id = "unknown",
            username = "unknown",
            acct = "unknown",
            displayname = "Unknown Account"
        )
    }
}

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

data class Field(
    val id: String?,
    val key: String,
    val value: String,
    val valueHtml: String?,
    val isVerified: Boolean
)