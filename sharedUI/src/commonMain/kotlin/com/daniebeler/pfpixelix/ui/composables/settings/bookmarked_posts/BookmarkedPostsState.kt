package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import com.daniebeler.pfpixelix.domain.model.Post

data class BookmarkedPostsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val bookmarkedPosts: List<Post> = emptyList(),
    val error: String = ""
)
