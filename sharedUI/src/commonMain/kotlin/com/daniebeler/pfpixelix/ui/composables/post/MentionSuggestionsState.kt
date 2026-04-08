package com.daniebeler.pfpixelix.ui.composables.post

import com.daniebeler.pfpixelix.domain.model.Account

data class SuggestionsState(
    val isLoading: Boolean = false,
    val suggestions: List<String> = emptyList(),
    val error: String = ""
)
