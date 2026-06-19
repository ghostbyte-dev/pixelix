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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingPostsViewModel @Inject constructor(
    private val exploreService: ExploreService
) : ViewModel() {

    var trendingState by mutableStateOf(TrendingPostsState())
        private set

    fun getTrendingPosts(timeRange: String, refreshing: Boolean = false) {
        if (!refreshing && trendingState.trendingPosts.isNotEmpty()) {
            return
        }
        exploreService.getTrendingPosts(timeRange).onEach { result ->
            trendingState = when (result) {
                is Resource.Success -> {
                    val endReached = result.data.data.isEmpty()

                    TrendingPostsState(trendingPosts = result.data.data, nextId = result.data.next, endReached = endReached)
                }

                is Resource.Error -> {
                    TrendingPostsState(error = result.message)
                }

                is Resource.Loading -> {
                    trendingState.copy(isLoading = true, isRefreshing = refreshing)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getTrendingPostsPaginated(timeRange: String) {
        if (trendingState.trendingPosts.isNotEmpty() && !trendingState.isLoading && !trendingState.endReached) {
            exploreService.getTrendingPosts(timeRange, trendingState.nextId).onEach { result ->
                trendingState = when (result) {
                    is Resource.Success -> {
                        val endReached = result.data.data.isEmpty()

                        TrendingPostsState(trendingPosts = trendingState.trendingPosts + result.data.data, nextId = result.data.next, endReached = endReached)
                    }

                    is Resource.Error -> {
                        TrendingPostsState(error = result.message)
                    }

                    is Resource.Loading -> {
                        trendingState.copy(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}