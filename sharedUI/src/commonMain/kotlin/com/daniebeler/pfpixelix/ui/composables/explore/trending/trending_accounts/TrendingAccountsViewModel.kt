package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.service.hashtag.SearchService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingAccountsViewModel @Inject constructor(
    private val searchService: SearchService
) : ViewModel() {
    var trendingAccountsState by mutableStateOf(TrendingAccountsState())
    var accountRelationShipsState by mutableStateOf(AccountRelationshipsState())

    init {
        getTrendingAccountsState()
    }

    fun getTrendingAccountsState(refreshing: Boolean = false) {
        if (refreshing || trendingAccountsState.trendingAccounts.isEmpty()) {
            searchService.getTrendingAccounts().onEach { result ->
                trendingAccountsState = when (result) {
                    is Resource.Success -> {
                        result.data?.let { getRelationships(it) }
                        TrendingAccountsState(trendingAccounts = result.data ?: emptyList())
                    }

                    is Resource.Error -> {
                        TrendingAccountsState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }

                    is Resource.Loading -> {
                        TrendingAccountsState(
                            isLoading = true,
                            isRefreshing = refreshing,
                            trendingAccounts = trendingAccountsState.trendingAccounts
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getRelationships(accounts: List<Account>) {
        val accountIds: List<String> = accounts.map { account: Account -> account.id }

        searchService.getRelationships(accountIds).onEach { result ->
            accountRelationShipsState = when (result) {
                is Resource.Success -> {
                    AccountRelationshipsState(accountRelationships = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    AccountRelationshipsState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Loading -> {
                    AccountRelationshipsState(
                        isLoading = true,
                        accountRelationships = accountRelationShipsState.accountRelationships
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}