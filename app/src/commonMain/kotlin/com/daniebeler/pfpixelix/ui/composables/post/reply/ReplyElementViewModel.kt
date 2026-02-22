package com.daniebeler.pfpixelix.ui.composables.post.reply

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.editor.PostEditorService
import com.daniebeler.pfpixelix.domain.service.post.PostService
import com.daniebeler.pfpixelix.domain.service.suggestions.SuggestionsManager
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class ReplyElementViewModel @Inject constructor(
    private val postEditorService: PostEditorService,
    private val postService: PostService,
    val suggestionsManager: SuggestionsManager
    ): ViewModel()
{
    var repliesState by mutableStateOf(RepliesState())
    var likedReply by mutableStateOf(false)
    var newReplyState by mutableStateOf(OwnReplyState())
    var replyText by mutableStateOf(TextFieldValue())
    fun onInit(reply: Post, myAccountId: String) {
        likedReply = reply.likedBy?.id == myAccountId
    }

    fun loadReplies(replyId: String) {
        postService.getReplies(replyId).onEach { result ->
            repliesState = when (result) {
                is Resource.Success -> {
                    RepliesState(replies = result.data?.descendants ?: emptyList())
                }

                is Resource.Error -> {
                    RepliesState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    RepliesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createReply(replyId: String, replyText: String, myAccountId: String) {
        if (replyText.isNotBlank()) {
            postService.createReply(replyId, replyText).onEach { result ->
                newReplyState = when (result) {
                    is Resource.Success -> {
                        loadReplies(replyId)
                        OwnReplyState(reply = result.data)
                    }

                    is Resource.Error -> {
                        OwnReplyState(error = result.message ?: "An unexpected error occurred")
                    }

                    is Resource.Loading -> {
                        OwnReplyState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun deleteReply(postId: String) {
        postEditorService.deletePost(postId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    repliesState = repliesState.copy(replies = repliesState.replies.filter { it.id != postId })
                }

                is Resource.Error -> {
                    Logger.e(result.message)
                }
                is Resource.Loading -> {
                    Logger.v("is loading")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun likeReply(postId: String) {
        postService.likePost(postId).onEach {result ->
            when (result) {
                is Resource.Success -> {
                    likedReply = true
                }

                is Resource.Error -> {
                    likedReply = false
                }

                is Resource.Loading -> {
                    Logger.v("is loading")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unlikeReply(postId: String) {
        postService.unlikePost(postId).onEach {result ->
            when (result) {
                is Resource.Success -> {
                    likedReply = false
                }

                is Resource.Error -> {
                    likedReply = true
                }

                is Resource.Loading -> {
                    Logger.v("is loading")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateReplyText(newReplyText: TextFieldValue) {
        replyText = newReplyText
        suggestionsManager.changeText(newReplyText, viewModelScope)
    }
}