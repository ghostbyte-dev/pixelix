package com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags

import com.daniebeler.pfpixelix.domain.model.Tag

data class FollowedHashtagsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val followedHashtags: List<Tag> = emptyList(),
    val error: String = ""
)
