package com.daniebeler.pfpixelix.ui.composables.post.reply

import com.daniebeler.pfpixelix.domain.model.Post

data class OwnReplyState(
    val isLoading: Boolean = false,
    val reply: Post? = null,
    val error: String = ""
)
