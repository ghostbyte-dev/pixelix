package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.post.PostService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.collections.plus

class BookmarkedPostsViewModel @Inject constructor(
    private val postService: PostService,
    private val prefs: UserPreferences,
) : ViewModel() {

    var bookmarkedPostsState by mutableStateOf(BookmarkedPostsState())
    var view by mutableStateOf(ViewEnum.Grid)

    init {
        getBookmarkedPosts()
        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = if (res) ViewEnum.Grid else ViewEnum.Timeline
            }
        }
    }

    fun getBookmarkedPosts(refreshing: Boolean = false) {
        postService.getBookmarkedPosts().onEach { result ->
            bookmarkedPostsState = when (result) {
                is Resource.Success -> {
                    BookmarkedPostsState(
                        bookmarkedPosts = result.data.data,
                        nextCursor = result.data.next ?: ""
                    )
                }

                is Resource.Error -> {
                    BookmarkedPostsState(
                        error = result.message,
                        bookmarkedPosts = bookmarkedPostsState.bookmarkedPosts
                    )
                }

                is Resource.Loading -> {
                    BookmarkedPostsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        bookmarkedPosts = bookmarkedPostsState.bookmarkedPosts
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getItemsPaginated() {
        if (bookmarkedPostsState.bookmarkedPosts.isNotEmpty() && !bookmarkedPostsState.isLoading && bookmarkedPostsState.nextCursor.isNotEmpty()) {
            postService.getBookmarkedPosts(bookmarkedPostsState.nextCursor).onEach { result ->
                bookmarkedPostsState = when (result) {
                    is Resource.Success -> {
                        BookmarkedPostsState(
                            bookmarkedPosts = bookmarkedPostsState.bookmarkedPosts + (result.data.data),
                            nextCursor = result.data.next ?: "",
                            error = "",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    is Resource.Error -> {
                        BookmarkedPostsState(
                            bookmarkedPosts = bookmarkedPostsState.bookmarkedPosts,
                            error = result.message,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    is Resource.Loading -> {
                        BookmarkedPostsState(
                            bookmarkedPosts = bookmarkedPostsState.bookmarkedPosts,
                            error = "",
                            isLoading = true,
                            isRefreshing = false
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun refresh() {
        getBookmarkedPosts(true)
    }

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView == ViewEnum.Grid
    }
}