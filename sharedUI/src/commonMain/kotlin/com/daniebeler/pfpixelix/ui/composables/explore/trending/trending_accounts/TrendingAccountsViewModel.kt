package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts.TrendingPostsState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingAccountsViewModel @Inject constructor(
    private val exploreService: ExploreService, private val session: Session
) : ViewModel() {
    val capabilities: Capabilities = session.capabilities.value
    var trendingAccountsState by mutableStateOf(TrendingAccountsState())

    var timeRange by mutableStateOf(TrendingRange.DAILY)

    init {
        getTrendingAccountsState()
    }

    fun getTrendingAccountsState(refreshing: Boolean = false) {
        if (!refreshing && trendingAccountsState.trendingAccounts.isNotEmpty()) return
        fetchAccounts(nextId = null, isRefreshing = refreshing)
    }

    fun getTrendingAccountsPaginated() {
        if (trendingAccountsState.isLoading || trendingAccountsState.endReached || trendingAccountsState.nextId == null || trendingAccountsState.trendingAccounts.isEmpty()) {
            return
        }

        fetchAccounts(nextId = trendingAccountsState.nextId, isRefreshing = false)
    }

    private fun fetchAccounts(nextId: String?, isRefreshing: Boolean) {
        exploreService.getTrendingAccounts(timeRange, nextId).onEach { result ->
            trendingAccountsState = when (result) {
                is Resource.Success -> {
                    val newAccounts = result.data.data
                    val updatedAccounts =
                        if (nextId == null) newAccounts else trendingAccountsState.trendingAccounts + newAccounts

                    trendingAccountsState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        trendingAccounts = updatedAccounts,
                        nextId = result.data.next,
                        endReached = newAccounts.isEmpty() || result.data.next == null,
                        error = ""
                    )
                }

                is Resource.Error -> {
                    trendingAccountsState.copy(
                        isLoading = false, isRefreshing = false, error = result.message
                    )
                }

                is Resource.Loading -> {
                    trendingAccountsState.copy(
                        isLoading = true, isRefreshing = isRefreshing
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun changeTimeRange(range: TrendingRange) {
        if (range != timeRange) {
            timeRange = range
            trendingAccountsState = TrendingAccountsState()
            getTrendingAccountsState()
        }
    }
}