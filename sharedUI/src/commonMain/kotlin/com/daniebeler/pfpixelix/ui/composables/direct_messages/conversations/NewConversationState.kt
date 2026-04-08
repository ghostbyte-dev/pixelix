package com.daniebeler.pfpixelix.ui.composables.direct_messages.conversations

import com.daniebeler.pfpixelix.domain.model.Account

data class NewConversationState(
    val isLoading: Boolean = false,
    val error: String = "",
    val suggestions: List<Account> = emptyList()
)
