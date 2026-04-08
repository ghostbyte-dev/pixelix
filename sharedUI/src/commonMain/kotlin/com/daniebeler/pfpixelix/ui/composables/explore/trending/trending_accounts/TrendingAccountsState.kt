package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import com.daniebeler.pfpixelix.domain.model.Account

data class TrendingAccountsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val trendingAccounts: List<Account> = emptyList(),
    val error: String = ""
)
