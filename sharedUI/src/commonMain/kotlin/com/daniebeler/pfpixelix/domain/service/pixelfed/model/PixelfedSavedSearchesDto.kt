package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.SavedSearchItem
import com.daniebeler.pfpixelix.domain.model.SavedSearchType
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedSavedSearchesDto(
    @SerialName("account_data") val accountData: Map<String, List<PixelfedSavedSearchItemDto>> = emptyMap()
)

@Serializable
data class PixelfedSavedSearchItemDto(
    @SerialName("saved_search_type") val savedSearchType: PixelfedSavedSearchTypeDto = PixelfedSavedSearchTypeDto.SEARCH,
    @SerialName("value") val value: String,
    @SerialName("account") val account: PixelfedAccountDto? = null
)

@Serializable
enum class PixelfedSavedSearchTypeDto {
    @SerialName("Account") ACCOUNT,
    @SerialName("Hashtag") HASHTAG,
    @SerialName("Search") SEARCH
}

fun PixelfedSavedSearchesDto.toDomain(): SavedSearches {
    return SavedSearches(
        accountData = this.accountData.mapValues { (_, items) ->
            items.map { it.toDomain() }
        }
    )
}

fun PixelfedSavedSearchItemDto.toDomain(): SavedSearchItem {
    return SavedSearchItem(
        savedSearchType = this.savedSearchType.toDomain(),
        value = this.value,
        account = this.account?.toDomain()
    )
}

fun PixelfedSavedSearchTypeDto.toDomain(): SavedSearchType = when (this) {
    PixelfedSavedSearchTypeDto.ACCOUNT -> SavedSearchType.Account
    PixelfedSavedSearchTypeDto.HASHTAG -> SavedSearchType.Hashtag
    PixelfedSavedSearchTypeDto.SEARCH  -> SavedSearchType.Search
}

fun SavedSearches.toDto(): PixelfedSavedSearchesDto {
    return PixelfedSavedSearchesDto(
        accountData = this.accountData.mapValues { (_, items) ->
            items.map { it.toDto() }
        }
    )
}

fun SavedSearchItem.toDto(): PixelfedSavedSearchItemDto {
    return PixelfedSavedSearchItemDto(
        savedSearchType = when (this.savedSearchType) {
            SavedSearchType.Account -> PixelfedSavedSearchTypeDto.ACCOUNT
            SavedSearchType.Hashtag -> PixelfedSavedSearchTypeDto.HASHTAG
            SavedSearchType.Search  -> PixelfedSavedSearchTypeDto.SEARCH
        },
        value = this.value,
        account = this.account?.let {
            PixelfedAccountDto(
                id = it.id,
                username = it.username,
                acct = it.acct,
                displayname = it.displayname,
                avatar = it.avatar,
                followersCount = it.followersCount,
                followingCount = it.followingCount,
                postsCount = it.postsCount,
                website = it.website,
                note = it.note,
                url = it.url,
                locked = it.locked,
                createdAt = it.createdAt,
                isAdmin = it.isAdmin,
                pronouns = it.pronouns
            )
        }
    )
}