package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedSearches(
    val accountData: Map<String, List<SavedSearchItem>> = emptyMap()
)

@Serializable
data class SavedSearchItem(
    val savedSearchType: SavedSearchType = SavedSearchType.Search,
    val value: String,
    val account: SavedSearchesAccount?
)


@Serializable
data class SavedSearchesAccount(
    val id: String = "",
    val username: String = "",
    val acct: String = "",
    val displayname: String? = null,
    val avatar: String = "",
    val followersCount: Int = 0,
    val url: String? = ""
)

fun SavedSearchesAccount.toDomain(): Account {
    return Account(
        id = this.id,
        username = this.username,
        acct = this.acct,
        displayname = this.displayname,
        avatar = this.avatar,
        followersCount = this.followersCount,
        followingCount = 0,
        postsCount = 0,
        website = "",
        note = "",
        url = this.url ?: "",
        locked = false,
        createdAt = "",
        isAdmin = false,
        pronouns = emptyList()
    )
}



enum class SavedSearchType {
    Account, Hashtag, Search
}