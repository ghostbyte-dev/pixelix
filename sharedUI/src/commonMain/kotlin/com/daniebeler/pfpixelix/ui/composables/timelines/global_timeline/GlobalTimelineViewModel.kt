package com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline

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

class GlobalTimelineViewModel @Inject constructor(
    private val timelineService: TimelineService
) : ViewModel() {

    var globalTimelineState by mutableStateOf(GlobalTimelineState())

    init {
        getItemsFirstLoad(false)
    }

    private fun getItemsFirstLoad(refreshing: Boolean) {
        if (globalTimelineState.globalTimeline.isNotEmpty() && !refreshing) {
            return
        }
        timelineService.getGlobalTimeline().onEach { result ->
            globalTimelineState = when (result) {
                is Resource.Success -> {
                    GlobalTimelineState(
                        globalTimeline = result.data ?: emptyList(),
                        error = "",
                        isLoading = false,
                        refreshing = false
                    )
                }

                is Resource.Error -> {
                    GlobalTimelineState(
                        globalTimeline = globalTimelineState.globalTimeline,
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        refreshing = false
                    )
                }

                is Resource.Loading -> {
                    GlobalTimelineState(
                        globalTimeline = globalTimelineState.globalTimeline,
                        error = "",
                        isLoading = true,
                        refreshing = refreshing
                    )
                }
            }
        }.launchIn(viewModelScope)

    }

    fun getItemsPaginated() {
        if (globalTimelineState.globalTimeline.isNotEmpty() && !globalTimelineState.isLoading) {
            timelineService.getGlobalTimeline(globalTimelineState.globalTimeline.last().id).onEach { result ->
                globalTimelineState = when (result) {
                    is Resource.Success -> {
                        GlobalTimelineState(
                            globalTimeline = globalTimelineState.globalTimeline + (result.data
                                ?: emptyList()), error = "", isLoading = false, refreshing = false
                        )
                    }

                    is Resource.Error -> {
                        GlobalTimelineState(
                            globalTimeline = globalTimelineState.globalTimeline,
                            error = result.message ?: "An unexpected error occurred",
                            isLoading = false,
                            refreshing = false
                        )
                    }

                    is Resource.Loading -> {
                        GlobalTimelineState(
                            globalTimeline = globalTimelineState.globalTimeline,
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
        globalTimelineState =
            globalTimelineState.copy(globalTimeline = globalTimelineState.globalTimeline.filter { post -> post.id != postId })
    }

    fun postGetsUpdated(post: Post) {
        globalTimelineState = globalTimelineState.copy(globalTimeline = globalTimelineState.globalTimeline.map {
            if (it.id == post.id) {
                post
            } else {
                it
            }
        })
    }
}