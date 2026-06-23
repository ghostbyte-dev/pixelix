package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountsState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingHashtagsViewModel @Inject constructor(
    private val exploreService: ExploreService,
    session: Session
) : ViewModel() {
    val capabilities: Capabilities = session.capabilities.value

    var trendingHashtagsState by mutableStateOf(TrendingHashtagsState())

    var timeRange by mutableStateOf(TrendingRange.DAILY)

    init {
        getTrendingHashtags()
    }

    fun getTrendingHashtags(refreshing: Boolean = false) {
        exploreService.getTrendingHashtags(timeRange).onEach { result ->
            trendingHashtagsState = when (result) {
                is Resource.Success -> {
                    TrendingHashtagsState(trendingHashtags = result.data.data)
                }

                is Resource.Error -> {
                    TrendingHashtagsState(error = result.message)
                }

                is Resource.Loading -> {
                    TrendingHashtagsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        trendingHashtags = trendingHashtagsState.trendingHashtags
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun changeTimeRange(range: TrendingRange) {
        if (range != timeRange) {
            timeRange = range
            trendingHashtagsState = TrendingHashtagsState()
            getTrendingHashtags()
        }
    }
}