package com.daniebeler.pfpixelix.ui.composables.edit_post

import com.daniebeler.pfpixelix.domain.model.Post

data class EditPostState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String = ""
)
