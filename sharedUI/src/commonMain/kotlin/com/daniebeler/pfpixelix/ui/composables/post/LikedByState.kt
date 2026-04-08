package com.daniebeler.pfpixelix.ui.composables.post

import com.daniebeler.pfpixelix.domain.model.Account

data class LikedByState(
    val isLoading: Boolean = false,
    val likedBy: List<Account> = emptyList(),
    val error: String = ""
)
