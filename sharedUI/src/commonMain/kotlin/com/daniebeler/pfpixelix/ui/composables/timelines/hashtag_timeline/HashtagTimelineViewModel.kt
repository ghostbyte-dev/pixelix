package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.widgets.PaginatedPostsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

data class HashtagState(
    val isLoading: Boolean = false,
    val hashtag: Tag? = null,
    val error: String = ""
)

class HashtagTimelineViewModel @Inject constructor(
    private val exploreService: ExploreService,
    private val timelineService: TimelineService,
    userPreferences: UserPreferences
) : PaginatedPostsViewModel(userPreferences) {

    var hashtagState by mutableStateOf(HashtagState())
        private set

    private var currentHashtag: String = ""

    fun init(hashtag: String) {
        if (currentHashtag != hashtag) {
            currentHashtag = hashtag
            getHashtagInfo(hashtag)
            loadItems(refreshing = false)
        }
    }

    override fun fetchPage(maxId: String?) = timelineService.getHashtagTimeline(currentHashtag, maxId)

    fun getHashtagInfo(hashtag: String) {
        exploreService.getHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> HashtagState(hashtag = result.data)
                is Resource.Error -> HashtagState(error = result.message ?: "An unexpected error occurred")
                is Resource.Loading -> HashtagState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun followHashtag() {
        val hashtag = currentHashtag
        exploreService.followHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> HashtagState(hashtag = result.data.copy(following = true), isLoading = false)
                is Resource.Error -> HashtagState(error = result.message ?: "An unexpected error occurred", hashtag = hashtagState.hashtag)
                is Resource.Loading -> HashtagState(isLoading = true, hashtag = hashtagState.hashtag)
            }
        }.launchIn(viewModelScope)
    }

    fun unfollowHashtag() {
        val hashtag = currentHashtag
        exploreService.unfollowHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> HashtagState(hashtag = hashtagState.hashtag?.copy(following = false), isLoading = false)
                is Resource.Error -> HashtagState(error = result.message, hashtag = hashtagState.hashtag)
                is Resource.Loading -> HashtagState(isLoading = true, hashtag = hashtagState.hashtag)
            }
        }.launchIn(viewModelScope)
    }
}