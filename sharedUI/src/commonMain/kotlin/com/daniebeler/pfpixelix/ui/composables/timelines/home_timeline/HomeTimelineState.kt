package com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline

import com.daniebeler.pfpixelix.domain.model.Post

data class HomeTimelineState(
    var isLoading: Boolean = false,
    var refreshing: Boolean = false,
    var homeTimeline: List<Post> = emptyList(),
    var error: String = ""
)
