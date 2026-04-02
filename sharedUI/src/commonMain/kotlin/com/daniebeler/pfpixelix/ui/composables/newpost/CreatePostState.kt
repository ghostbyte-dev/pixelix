package com.daniebeler.pfpixelix.ui.composables.newpost

import com.daniebeler.pfpixelix.domain.model.Post

data class CreatePostState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String = ""
)
