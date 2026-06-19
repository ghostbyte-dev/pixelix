package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingHashtagsViewModel @Inject constructor(
    private val exploreService: ExploreService
) : ViewModel() {

    var trendingHashtagsState by mutableStateOf(TrendingHashtagsState())

    fun getTrendingHashtags(range: String, refreshing: Boolean = false) {
        exploreService.getTrendingHashtags(range).onEach { result ->
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
}