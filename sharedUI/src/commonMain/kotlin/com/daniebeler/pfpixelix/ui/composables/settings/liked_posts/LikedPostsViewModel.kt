package com.daniebeler.pfpixelix.ui.composables.settings.liked_posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.post.PostService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class LikedPostsViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel() {

    var likedPostsState by mutableStateOf(LikedPostsState())

    init {
        getItemsFirstLoad()
    }

    fun getItemsFirstLoad(refreshing: Boolean = false) {
        postService.getLikedPosts().onEach { result ->
            likedPostsState = when (result) {
                is Resource.Success -> {
                    LikedPostsState(
                        likedPosts = result.data?.posts ?: emptyList(),
                        nextMaxId = result.data?.nextId ?: ""
                    )
                }

                is Resource.Error -> {
                    LikedPostsState(
                        likedPosts = likedPostsState.likedPosts,
                        error = result.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Loading -> {
                    LikedPostsState(
                        likedPosts = likedPostsState.likedPosts,
                        isLoading = true,
                        isRefreshing = refreshing
                    )
                }
            }
        }.launchIn(viewModelScope)

    }

    fun getItemsPaginated() {
        if (likedPostsState.likedPosts.isNotEmpty() && !likedPostsState.isLoading && likedPostsState.nextMaxId.isNotEmpty()) {
            postService.getLikedPosts(likedPostsState.nextMaxId).onEach { result ->
                likedPostsState = when (result) {
                    is Resource.Success -> {
                        LikedPostsState(
                            likedPosts = likedPostsState.likedPosts + (result.data?.posts
                                ?: emptyList()),
                            nextMaxId = result.data?.nextId ?: "",
                            error = "",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    is Resource.Error -> {
                        LikedPostsState(
                            likedPosts = likedPostsState.likedPosts,
                            error = result.message ?: "An unexpected error occurred",
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    is Resource.Loading -> {
                        LikedPostsState(
                            likedPosts = likedPostsState.likedPosts,
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
        getItemsFirstLoad(true)
    }

}