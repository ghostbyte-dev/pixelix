package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class CustomNotificationViewModel @Inject constructor(
    private val postService: PostService,
    private val accountService: AccountService
): ViewModel() {
    var ancestor by mutableStateOf<Post?>(null)
    val followRequestState = mutableStateOf(FollowRequestState())

    fun loadAncestor(postId: String) {
        postService.getPostById(postId).onEach { result ->
            if (result is Resource.Success) {
                ancestor = result.data!!
            }
        }.launchIn(viewModelScope)
    }

    fun acceptFollowRequest(accountId: String, removeNotification: () -> Unit) {
        accountService.acceptFollowRequest(accountId).onEach { result ->
            when(result) {
                is Resource.Success -> {
                    removeNotification()
                    followRequestState.value = FollowRequestState(relationship = result.data)
                }
                is Resource.Error -> {
                    followRequestState.value = FollowRequestState(error = result.message)
                }
                is Resource.Loading -> {
                    followRequestState.value = FollowRequestState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun rejectFollowRequest(accountId: String, removeNotification: () -> Unit) {
        accountService.rejectFollowRequest(accountId).onEach { result ->
            when(result) {
                is Resource.Success -> {
                    removeNotification()
                    followRequestState.value = FollowRequestState(relationship = result.data)
                }
                is Resource.Error -> {
                    followRequestState.value = FollowRequestState(error = result.message)
                }
                is Resource.Loading -> {
                    followRequestState.value = FollowRequestState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}