package com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts

import com.daniebeler.pfpixelix.domain.model.Account

data class BlockedAccountsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val blockedAccounts: List<Account> = emptyList(),
    val error: String = ""
)
