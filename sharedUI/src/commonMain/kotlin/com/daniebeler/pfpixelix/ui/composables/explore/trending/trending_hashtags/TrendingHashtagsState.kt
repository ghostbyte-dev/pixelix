package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import com.daniebeler.pfpixelix.domain.model.Tag

data class TrendingHashtagsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val trendingHashtags: List<Tag> = emptyList(),
    val error: String = ""
)
