package com.daniebeler.pfpixelix.ui.composables.profile

import com.daniebeler.pfpixelix.domain.model.Account

data class MutualFollowersState(
    val isLoading: Boolean = false,
    val mutualFollowers: List<Account> = emptyList(),
    val error: String = ""
)
