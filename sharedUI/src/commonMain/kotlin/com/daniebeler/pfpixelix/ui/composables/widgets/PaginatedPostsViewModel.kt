package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.timelines.TimelineState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class PaginatedPostsViewModel : ViewModel() {

    var timelineState by mutableStateOf(TimelineState(isLoading = true))
        protected set

    protected abstract fun fetchPage(maxId: String?): Flow<Resource<PaginatedResponse<List<Post>>>>

    protected fun loadItems(refreshing: Boolean) {
        if (timelineState.posts.isNotEmpty() && !refreshing) return
        fetchPage(null).onEach { result ->
            timelineState = when (result) {
                is Resource.Success -> TimelineState(posts = result.data.data, nextId = result.data.next)
                is Resource.Error -> timelineState.copy(
                    error = result.message,
                    isLoading = false,
                    isRefreshing = false
                )
                is Resource.Loading -> timelineState.copy(isLoading = true, isRefreshing = refreshing)
            }
        }.launchIn(viewModelScope)
    }

    fun getItemsPaginated() {
        if (timelineState.posts.isEmpty() || timelineState.isLoading) return
        fetchPage(timelineState.nextId).onEach { result ->
            timelineState = when (result) {
                is Resource.Success -> timelineState.copy(
                    posts = timelineState.posts + (result.data.data),
                    nextId = result.data.next,
                    isLoading = false,
                    isRefreshing = false,
                    error = ""
                )
                is Resource.Error -> timelineState.copy(
                    error = result.message,
                    isLoading = false,
                    isRefreshing = false
                )
                is Resource.Loading -> timelineState.copy(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun refresh() = loadItems(true)

    fun postGetsDeleted(postId: String) {
        timelineState = timelineState.copy(posts = timelineState.posts.filter { it.id != postId })
    }

    fun postGetsUpdated(post: Post) {
        timelineState = timelineState.copy(posts = timelineState.posts.map {
            if (it.id == post.id) post else it
        })
    }
}
