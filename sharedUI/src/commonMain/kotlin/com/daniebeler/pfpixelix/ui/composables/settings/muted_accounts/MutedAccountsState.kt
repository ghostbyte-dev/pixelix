package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import com.daniebeler.pfpixelix.domain.model.MutedAccount

data class MutedAccountsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val mutedAccounts: List<MutedAccount> = emptyList(),
    val error: String = ""
)
