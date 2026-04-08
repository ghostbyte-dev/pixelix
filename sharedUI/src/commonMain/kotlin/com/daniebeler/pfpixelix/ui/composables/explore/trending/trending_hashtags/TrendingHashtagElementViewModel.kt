package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.hashtag.SearchService
import com.daniebeler.pfpixelix.domain.service.timeline.TimelineService
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingHashtagElementViewModel @Inject constructor(
    private val timelineService: TimelineService,
    private val searchService: SearchService
) : ViewModel() {

    var postsState by mutableStateOf(TrendingHashtagPostsState())
    var hashtagState by mutableStateOf(HashtagState())

    fun loadItems(hashtag: String) {
        if (postsState.posts.isEmpty()) {
            timelineService.getHashtagTimeline(hashtag, limit = 39).onEach { result ->
                postsState = when (result) {
                    is Resource.Success -> {
                        TrendingHashtagPostsState(
                            posts = result.data ?: emptyList(), error = "", isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        TrendingHashtagPostsState(
                            posts = postsState.posts,
                            error = result.message ?: "An unexpected error occurred",
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

    fun getHashtagInfo(hashtag: String) {
        if (hashtagState.hashtag == null) {
            searchService.getHashtag(hashtag).onEach { result ->
                hashtagState = when (result) {
                    is Resource.Success -> {
                        HashtagState(hashtag = result.data)
                    }

                    is Resource.Error -> {
                        HashtagState(error = result.message ?: "An unexpected error occurred")
                    }

                    is Resource.Loading -> {
                        HashtagState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }

    }
}