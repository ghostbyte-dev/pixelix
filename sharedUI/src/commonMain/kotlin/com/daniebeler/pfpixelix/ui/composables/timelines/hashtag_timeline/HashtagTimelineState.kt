package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import com.daniebeler.pfpixelix.domain.model.Post

data class HashtagTimelineState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val hashtagTimeline: List<Post> = emptyList(),
    val error: String = ""
)
