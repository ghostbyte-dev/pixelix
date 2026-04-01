package com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline

import com.daniebeler.pfpixelix.domain.model.Post

data class GlobalTimelineState(
    val isLoading: Boolean = false,
    val refreshing: Boolean = false,
    val globalTimeline: List<Post> = emptyList(),
    val error: String = ""
)
