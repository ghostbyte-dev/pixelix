package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import com.daniebeler.pfpixelix.ui.composables.timelines.TimelineState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class HashtagTimelineViewModel @Inject constructor(
    private val exploreService: ExploreService,
    private val timelineService: TimelineService,
    private val prefs: UserPreferences
) : ViewModel() {

    var timelineState by mutableStateOf(TimelineState())
    var hashtagState by mutableStateOf(HashtagState())
    var view by mutableStateOf(ViewEnum.Grid)

    var relatedHashtags by mutableStateOf<List<RelatedHashtag>>(emptyList())



    init {
        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = ViewEnum.getView(res)
            }
        }
    }

    fun refresh() {
        timelineState = timelineState.copy(isRefreshing = true)
        if (hashtagState.hashtag != null) {
            getItemsFirstLoad(hashtagState.hashtag!!.name, true)
        }
    }

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView.ordinal
    }

    fun getItemsFirstLoad(hashtag: String, refreshing: Boolean = false) {
        if (timelineState.posts.isNotEmpty() && !refreshing) {
            return
        }
        timelineService.getHashtagTimeline(hashtag).onEach { result ->
            timelineState = when (result) {
                is Resource.Success -> {
                    val endReached =
                        (result.data.data.size) < PixelfedApi.HASHTAG_TIMELINE_POSTS_LIMIT
                    TimelineState(
                        posts = result.data.data,
                        nextId = result.data.next,
                        error = "",
                        isLoading = false,
                        isRefreshing = false,
                        endReached = endReached
                    )
                }

                is Resource.Error -> {
                    TimelineState(
                        posts = timelineState.posts,
                        nextId = timelineState.nextId,
                        error = result.message,
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                is Resource.Loading -> {
                    TimelineState(
                        posts = timelineState.posts,
                        nextId = timelineState.nextId,
                        error = "",
                        isLoading = true,
                        isRefreshing = refreshing
                    )
                }
            }
        }.launchIn(viewModelScope)

    }

    fun getItemsPaginated(hashtag: String) {
        if (timelineState.posts.isNotEmpty() && !timelineState.isLoading && !timelineState.endReached) {
            timelineService.getHashtagTimeline(
                hashtag, timelineState.posts.last().id
            ).onEach { result ->
                timelineState = when (result) {
                    is Resource.Success -> {
                        val endReached = (result.data.data.size ?: 0) == 0
                        TimelineState(
                            posts = timelineState.posts + (result.data.data),
                            nextId = result.data.next,
                            error = "",
                            isLoading = false,
                            isRefreshing = false,
                            endReached = endReached
                        )
                    }

                    is Resource.Error -> {
                        TimelineState(
                            posts = timelineState.posts,
                            nextId = timelineState.nextId,
                            error = result.message ?: "An unexpected error occurred",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    is Resource.Loading -> {
                        TimelineState(
                            posts = timelineState.posts,
                            nextId = timelineState.nextId,
                            error = "",
                            isLoading = true,
                            isRefreshing = false
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getRelatedHashtags(hashtag: String) {
        exploreService.getRelatedHashtags(hashtag).onEach { result ->
            if (result is Resource.Success) {
                relatedHashtags = result.data
                Logger.v("juhuu" + result.data)
            } else {
                Logger.v("fief" + result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun postGetsDeleted(postId: String) {
        timelineState =
            timelineState.copy(posts = timelineState.posts.filter { post -> post.id != postId })
    }

    fun getHashtagInfo(hashtag: String) {
        exploreService.getHashtag(hashtag).onEach { result ->
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

    fun followHashtag(hashtag: String) {
        exploreService.followHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> {
                    val newHashtag = hashtagState.hashtag
                    if (newHashtag != null) {
                        HashtagState(hashtag = newHashtag.copy(following = true))
                    } else {
                        HashtagState(hashtag = result.data)
                    }
                }

                is Resource.Error -> {
                    HashtagState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    HashtagState(isLoading = true, hashtag = hashtagState.hashtag)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unfollowHashtag(hashtag: String) {
        exploreService.unfollowHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> {
                    val newHashtag = hashtagState.hashtag
                    if (newHashtag != null) {
                        HashtagState(hashtag = newHashtag.copy(following = false))
                    } else {
                        HashtagState(hashtag = result.data)
                    }
                }

                is Resource.Error -> {
                    HashtagState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    HashtagState(isLoading = true, hashtag = hashtagState.hashtag)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun postGetsUpdated(post: Post) {
        timelineState = timelineState.copy(posts = timelineState.posts.map {
            if (it.id == post.id) {
                post
            } else {
                it
            }
        })
    }
}
