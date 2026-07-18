package com.daniebeler.pfpixelix.ui.composables.single_post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.general.PostService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class SinglePostViewModel @Inject constructor(
    private val postService: PostService
) : ViewModel() {

    var postState by mutableStateOf(SinglePostState())

    fun getPost(postId: String, refreshing: Boolean = false) {
        postService.getPostById(postId).onEach { result ->
            postState = when (result) {
                is Resource.Success -> {
                    SinglePostState(post = result.data)
                }

                is Resource.Error -> {
                    SinglePostState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    postState.copy(isLoading = !refreshing, isRefreshing = refreshing)
                }
            }
        }.launchIn(viewModelScope)
    }
}