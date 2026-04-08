package com.daniebeler.pfpixelix.ui.composables.direct_messages.chat

import com.daniebeler.pfpixelix.domain.model.Message

data class NewMessageState (
    val isLoading: Boolean = false,
    val message: Message? = null,
    val error: String = ""
)
