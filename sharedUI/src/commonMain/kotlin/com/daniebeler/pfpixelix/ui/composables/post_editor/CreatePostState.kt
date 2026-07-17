package com.daniebeler.pfpixelix.ui.composables.post_editor

import com.daniebeler.pfpixelix.domain.model.Post

data class CreatePostState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String = ""
)
