package com.daniebeler.pfpixelix.ui.composables.post.reply

import com.daniebeler.pfpixelix.domain.service.general.ReplyNode

data class RepliesState(
    val isLoading: Boolean = false,
    val replies: List<ReplyNode> = emptyList(),
    val error: String = ""
)
