package com.daniebeler.pfpixelix.domain.model

data class SavedSearches(
    val accountData: Map<String, List<SavedSearchItem>> = emptyMap()
)

data class SavedSearchItem(
    val savedSearchType: SavedSearchType = SavedSearchType.Search,
    val value: String,
    val account: Account?
)

enum class SavedSearchType {
    Account, Hashtag, Search
}