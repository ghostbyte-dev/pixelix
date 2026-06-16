package com.daniebeler.pfpixelix.domain.model

data class Account(
    val id: String = "",
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
    val headerUrl: String? = null
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