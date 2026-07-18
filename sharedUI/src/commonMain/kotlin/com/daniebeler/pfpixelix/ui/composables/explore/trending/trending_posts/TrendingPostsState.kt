package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts

import com.daniebeler.pfpixelix.domain.model.Post

data class TrendingPostsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val trendingPosts: List<Post> = emptyList(),
    val error: String = "",
    val nextId: String? = null,
    val endReached: Boolean = false
)
