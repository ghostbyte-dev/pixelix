package com.daniebeler.pfpixelix.ui.composables.single_post

import com.daniebeler.pfpixelix.domain.model.Post

data class SinglePostState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val post: Post? = null,
    val error: String = ""
)
