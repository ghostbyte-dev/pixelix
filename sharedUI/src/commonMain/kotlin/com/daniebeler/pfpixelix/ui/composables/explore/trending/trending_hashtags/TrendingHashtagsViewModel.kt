package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingHashtagsViewModel @Inject constructor(
    private val exploreService: ExploreService,
    val timelineService: TimelineService,
    session: Session
) : ViewModel() {
    val capabilities: StateFlow<Capabilities> = session.capabilities

    var trendingHashtagsState by mutableStateOf(TrendingHashtagsState())

    var timeRange by mutableStateOf(TrendingRange.DAILY)

    init {
        getTrendingHashtags()
    }

    fun getTrendingHashtags(refreshing: Boolean = false) {
        // Prevent reloading if we already have items and aren't pulling to refresh
        if (!refreshing && trendingHashtagsState.trendingHashtags.isNotEmpty()) return

        fetchHashtags(nextId = null, isRefreshing = refreshing)
    }

    fun getTrendingHashtagsPaginated() {
        if (trendingHashtagsState.isLoading ||
            trendingHashtagsState.endReached ||
            trendingHashtagsState.nextId == null ||
            trendingHashtagsState.trendingHashtags.isEmpty()) {
            return
        }

        fetchHashtags(nextId = trendingHashtagsState.nextId, isRefreshing = false)
    }

    private fun fetchHashtags(nextId: String?, isRefreshing: Boolean) {
        exploreService.getTrendingHashtags(timeRange, nextId).onEach { result ->
            trendingHashtagsState = when (result) {
                is Resource.Success -> {
                    val newHashtags = result.data.data
                    val updatedHashtags = if (nextId == null) newHashtags else trendingHashtagsState.trendingHashtags + newHashtags

                    trendingHashtagsState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        trendingHashtags = updatedHashtags,
                        nextId = result.data.next,
                        endReached = newHashtags.isEmpty() || result.data.next == null,
                        error = ""
                    )
                }

                is Resource.Error -> {
                    trendingHashtagsState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    trendingHashtagsState.copy(
                        isLoading = true,
                        isRefreshing = isRefreshing
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