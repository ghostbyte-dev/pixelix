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
    private val exploreService: ExploreService,
    private val session: Session
) : ViewModel() {
    val capabilities: Capabilities = session.capabilities.value
    var trendingAccountsState by mutableStateOf(TrendingAccountsState())

    var timeRange by mutableStateOf(TrendingRange.DAILY)

    init {
        getTrendingAccountsState()
    }

    fun getTrendingAccountsState(refreshing: Boolean = false) {
        if (refreshing || trendingAccountsState.trendingAccounts.isEmpty()) {
            exploreService.getTrendingAccounts(timeRange).onEach { result ->
                trendingAccountsState = when (result) {
                    is Resource.Success -> {
                        TrendingAccountsState(trendingAccounts = result.data.data)
                    }

                    is Resource.Error -> {
                        TrendingAccountsState(
                            error = result.message
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

    fun changeTimeRange(range: TrendingRange) {
        if (range != timeRange) {
            timeRange = range
            trendingAccountsState = TrendingAccountsState()
            getTrendingAccountsState()
        }
    }
}