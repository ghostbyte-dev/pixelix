package com.daniebeler.pfpixelix.ui.composables.direct_messages.chat

import com.daniebeler.pfpixelix.domain.model.Chat

data class ChatState(
    val isLoading: Boolean = false,
    val chat: Chat? = null,
    val endReached: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String = ""
)
