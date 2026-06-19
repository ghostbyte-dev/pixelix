package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingHashtagElementViewModel @Inject constructor(
    private val timelineService: TimelineService
) : ViewModel() {

    var postsState by mutableStateOf(TrendingHashtagPostsState())
    fun loadItems(hashtag: String) {
        if (postsState.posts.isEmpty()) {
            timelineService.getHashtagTimeline(hashtag, limit = 39).onEach { result ->
                postsState = when (result) {
                    is Resource.Success -> {
                        TrendingHashtagPostsState(
                            posts = result.data.data, error = "", isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        TrendingHashtagPostsState(
                            posts = postsState.posts,
                            error = result.message,
                            isLoading = false
                        )
                    }

                    is Resource.Loading -> {
                        TrendingHashtagPostsState(
                            posts = postsState.posts, error = "", isLoading = true
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}