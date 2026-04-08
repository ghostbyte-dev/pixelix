package com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.service.hashtag.SearchService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class FollowedHashtagsViewModel @Inject constructor(
    private val searchService: SearchService
) : ViewModel() {

    var followedHashtagsState by mutableStateOf(FollowedHashtagsState())

    init {
        getFollowedHashtags()
    }

    fun getFollowedHashtags(refreshing: Boolean = false) {
        searchService.getFollowedHashtags().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data?.isNotEmpty() == false) {
                        followedHashtagsState = FollowedHashtagsState()
                    } else {
                        result.data!!.forEach {
                            getFollowedHashtagSingle(it)
                        }
                    }
                }

                is Resource.Error -> {
                    followedHashtagsState = FollowedHashtagsState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Loading -> {
                    followedHashtagsState = FollowedHashtagsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        followedHashtags = followedHashtagsState.followedHashtags
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getFollowedHashtagSingle(tag: Tag) {
        searchService.getHashtag(tag.name).onEach { result ->
            followedHashtagsState = when (result) {
                is Resource.Success -> {
                    FollowedHashtagsState(followedHashtags = followedHashtagsState.followedHashtags + result.data)
                }

                is Resource.Error -> {
                    FollowedHashtagsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    FollowedHashtagsState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}