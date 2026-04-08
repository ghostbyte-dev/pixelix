package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import com.daniebeler.pfpixelix.domain.model.Account

data class MutedAccountsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val mutedAccounts: List<Account> = emptyList(),
    val error: String = ""
)
