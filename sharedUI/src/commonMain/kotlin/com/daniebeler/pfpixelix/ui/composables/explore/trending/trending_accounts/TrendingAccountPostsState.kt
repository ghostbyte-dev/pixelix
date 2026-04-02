package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import com.daniebeler.pfpixelix.domain.model.Post

data class TrendingAccountPostsState (
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String = ""
)