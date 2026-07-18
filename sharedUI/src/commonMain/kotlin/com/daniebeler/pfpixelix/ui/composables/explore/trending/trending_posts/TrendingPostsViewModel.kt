package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class TrendingPostsViewModel @Inject constructor(
    private val exploreService: ExploreService,
    private val prefs: UserPreferences,
) : ViewModel() {

    var trendingState by mutableStateOf(TrendingPostsState())
        private set

    var view by mutableStateOf(ViewEnum.Grid)

    var timeRange by mutableStateOf(TrendingRange.DAILY)

    init {
        getTrendingPosts()

        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = ViewEnum.getView(res)
            }
        }
    }

    fun getTrendingPosts(refreshing: Boolean = false) {
        // If we are not refreshing and already have data, don't reload
        if (!refreshing && trendingState.trendingPosts.isNotEmpty()) return

        // Pass null as nextId to fetch the first page
        fetchPosts(nextId = null, isRefreshing = refreshing)
    }

    fun getTrendingPostsPaginated() {
        // Guard clause to check if pagination is safe/needed
        if (trendingState.isLoading || trendingState.endReached || trendingState.trendingPosts.isEmpty()) {
            return
        }

        fetchPosts(nextId = trendingState.nextId, isRefreshing = false)
    }

    private fun fetchPosts(nextId: String?, isRefreshing: Boolean) {
        exploreService.getTrendingPosts(timeRange, nextId).onEach { result ->
            trendingState = when (result) {
                is Resource.Success -> {
                    val newPosts = result.data.data
                    val updatedPosts =
                        if (nextId == null) newPosts else trendingState.trendingPosts + newPosts

                    trendingState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        trendingPosts = updatedPosts,
                        nextId = result.data.next,
                        endReached = newPosts.isEmpty(),
                        error = ""
                    )
                }

                is Resource.Error -> {
                    trendingState.copy(
                        isLoading = false, isRefreshing = false, error = result.message
                    )
                }

                is Resource.Loading -> {
                    trendingState.copy(
                        isLoading = true, isRefreshing = isRefreshing
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun changeTimeRange(range: TrendingRange) {
        if (range != timeRange) {
            timeRange = range
            trendingState = TrendingPostsState()
            getTrendingPosts()
        }

    }

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView.ordinal
    }
}