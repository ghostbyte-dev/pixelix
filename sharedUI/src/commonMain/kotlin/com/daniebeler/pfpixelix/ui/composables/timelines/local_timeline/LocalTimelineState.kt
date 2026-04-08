package com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline

import com.daniebeler.pfpixelix.domain.model.Post

data class LocalTimelineState(
    val isLoading: Boolean = false,
    val refreshing: Boolean = false,
    val localTimeline: List<Post> = emptyList(),
    val error: String = ""
)
