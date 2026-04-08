package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import com.daniebeler.pfpixelix.domain.model.Tag

data class HashtagState(
    val isLoading: Boolean = false,
    val hashtag: Tag? = null,
    val error: String = ""
)
