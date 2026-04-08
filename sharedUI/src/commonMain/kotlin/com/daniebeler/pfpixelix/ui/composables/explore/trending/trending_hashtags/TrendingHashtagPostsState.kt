package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import com.daniebeler.pfpixelix.domain.model.Post

data class TrendingHashtagPostsState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String = ""
)
