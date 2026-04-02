package com.daniebeler.pfpixelix.ui.composables.followers

import com.daniebeler.pfpixelix.domain.model.Account

data class FollowingState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val following: List<Account> = emptyList(),
    val error: String = ""
)
