package com.daniebeler.pfpixelix.ui.composables.timelines

import com.daniebeler.pfpixelix.domain.model.Post

data class TimelineState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val error: String = "",
    val nextId: String? = null
)
