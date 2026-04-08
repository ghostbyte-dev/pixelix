package com.daniebeler.pfpixelix.ui.composables.post.reply

import com.daniebeler.pfpixelix.domain.model.Post

data class RepliesState(
    val isLoading: Boolean = false,
    val replies: List<Post> = emptyList(),
    val error: String = ""
)
