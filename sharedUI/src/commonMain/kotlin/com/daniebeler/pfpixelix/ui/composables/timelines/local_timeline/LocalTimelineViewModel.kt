package com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.timeline.TimelineService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class LocalTimelineViewModel @Inject constructor(
    private val timelineService: TimelineService
) : ViewModel() {

    var localTimelineState by mutableStateOf(LocalTimelineState())

    init {
        getItemsFirstLoad(false)
    }

    private fun getItemsFirstLoad(refreshing: Boolean) {
        if (localTimelineState.localTimeline.isNotEmpty() && !refreshing) {
            return
        }
        timelineService.getLocalTimeline().onEach { result ->
            localTimelineState = when (result) {
                is Resource.Success -> {
                    LocalTimelineState(
                        localTimeline = result.data ?: emptyList(),
                        error = "",
                        isLoading = false,
                        refreshing = false
                    )
                }

                is Resource.Error -> {
                    LocalTimelineState(
                        localTimeline = localTimelineState.localTimeline,
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        refreshing = false
                    )
                }

                is Resource.Loading -> {
                    LocalTimelineState(
                        localTimeline = localTimelineState.localTimeline,
                        error = "",
                        isLoading = true,
                        refreshing = refreshing
                    )
                }
            }
        }.launchIn(viewModelScope)

    }

    fun getItemsPaginated() {
        if (localTimelineState.localTimeline.isNotEmpty() && !localTimelineState.isLoading) {
            timelineService.getLocalTimeline(localTimelineState.localTimeline.last().id).onEach { result ->
                localTimelineState = when (result) {
                    is Resource.Success -> {
                        LocalTimelineState(
                            localTimeline = localTimelineState.localTimeline + (result.data
                                ?: emptyList()), error = "", isLoading = false, refreshing = false
                        )
                    }

                    is Resource.Error -> {
                        LocalTimelineState(
                            localTimeline = localTimelineState.localTimeline,
                            error = result.message ?: "An unexpected error occurred",
                            isLoading = false,
                            refreshing = false
                        )
                    }

                    is Resource.Loading -> {
                        LocalTimelineState(
                            localTimeline = localTimelineState.localTimeline,
                            error = "",
                            isLoading = true,
                            refreshing = false
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun refresh() {
        getItemsFirstLoad(true)
    }

    fun postGetsDeleted(postId: String) {
        localTimelineState =
            localTimelineState.copy(localTimeline = localTimelineState.localTimeline.filter { post -> post.id != postId })
    }

    fun postGetsUpdated(post: Post) {
        localTimelineState = localTimelineState.copy(localTimeline = localTimelineState.localTimeline.map {
            if (it.id == post.id) {
                post
            } else {
                it
            }
        })
    }
}