package com.daniebeler.pfpixelix.ui.composables.settings.liked_posts

import com.daniebeler.pfpixelix.domain.model.Post

data class LikedPostsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val likedPosts: List<Post> = emptyList(),
    val nextLimit: String = "",
    val nextMaxId: String = "",
    val error: String = ""
)
