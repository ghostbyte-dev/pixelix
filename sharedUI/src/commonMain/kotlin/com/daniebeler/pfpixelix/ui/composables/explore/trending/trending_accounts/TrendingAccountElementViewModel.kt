package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingAccountElementViewModel @Inject constructor(
    private val postService: PostService,
    private val platform: Platform
) : ViewModel() {
    var postsState by mutableStateOf(TrendingAccountPostsState())

    fun loadItems(accountId: String, username: String) {
        if (postsState.posts.isEmpty()) {
            postService.getPostsOfAccount(accountId, username, limit = 9).onEach { result ->
                postsState = when (result) {
                    is Resource.Success -> {
                        TrendingAccountPostsState(
                            posts = result.data.data, error = "", isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        TrendingAccountPostsState(
                            posts = postsState.posts,
                            error = result.message,
                            isLoading = false
                        )
                    }

                    is Resource.Loading -> {
                        TrendingAccountPostsState(
                            posts = postsState.posts, error = "", isLoading = true
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun openUrl(url: String) {
        platform.openUrl(url)
    }
}