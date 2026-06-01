package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.LikedBy
import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.ReportObjectType
import com.daniebeler.pfpixelix.domain.service.account.AccountService
import com.daniebeler.pfpixelix.domain.service.editor.PostEditorService
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.instance.InstanceService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.post.PostService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.suggestions.HashtagMentionsSuggestionsManager
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.post.reply.OwnReplyState
import com.daniebeler.pfpixelix.ui.composables.post.reply.RepliesState
import com.daniebeler.pfpixelix.ui.composables.profile.RelationshipState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject


class PostViewModel @Inject constructor(
    private val postService: PostService,
    private val prefs: UserPreferences,
    private val postEditorService: PostEditorService,
    private val authService: AuthService,
    private val accountService: AccountService,
    private val platform: Platform,
    private val fileService: FileService,
    private val instanceService: InstanceService,
    val hashtagMentionsSuggestionsManager: HashtagMentionsSuggestionsManager
) : ViewModel() {

    var post: Post? by mutableStateOf(null)

    var repliesState by mutableStateOf(RepliesState())

    var ownReplyState by mutableStateOf(OwnReplyState())

    var likedByState by mutableStateOf(LikedByState())

    var deleteState by mutableStateOf(DeleteState())
    var deleteDialog: String? by mutableStateOf(null)
    var reportState by mutableStateOf<ReportState?>(null)
    var showPost: Boolean by mutableStateOf(false)

    var myAccountId: String? = null
    var myUsername: String? = null

    var isAltTextButtonHidden by mutableStateOf(false)
    var isInFocusMode by mutableStateOf(false)
    var isAutoplayVideos by mutableStateOf(true)
    var blurSensitiveContent by mutableStateOf(false)
    var instance: Instance? = null
    var replyText by mutableStateOf(TextFieldValue())

    var volume by mutableStateOf(prefs.enableVolume)
    var relationshipState by mutableStateOf(RelationshipState())

    init {
        myAccountId = authService.getCurrentSession()!!.accountId
        myUsername = authService.getCurrentSession()!!.username
        getInstance()
        viewModelScope.launch {
            prefs.hideAltTextButtonFlow.collect {
                isAltTextButtonHidden = it
            }
        }
        viewModelScope.launch {
            prefs.focusModeFlow.collect { isInFocusMode = it }
        }
        viewModelScope.launch {
            prefs.autoplayVideoFlow.collect { isAutoplayVideos = it }
        }
        viewModelScope.launch {
            prefs.blurSensitiveContentFlow.collect { blurSensitiveContent = it }
        }
    }

    fun toggleVolume(newVolume: Boolean) {
        volume = newVolume
        prefs.enableVolume = newVolume
    }

    fun updatePost(post: Post) {
        this.post = post
        getVolume()
    }

    private fun getVolume() {
        viewModelScope.launch {
            prefs.enableVolumeFlow.collect { res ->
                volume = res
            }
        }
    }

    private fun getInstance() {
        instanceService.getInstance().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    instance = result.data
                }

                is Resource.Error -> {
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deletePost(postId: String) {
        deleteDialog = null
        postEditorService.deletePost(postId).onEach { result ->
            deleteState = when (result) {
                is Resource.Success -> {
                    DeleteState(deleted = true)
                }

                is Resource.Error -> {
                    DeleteState(error = result.message)
                }

                is Resource.Loading -> {
                    DeleteState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleShowPost() {
        showPost = !showPost
    }

    fun loadReplies(postId: String) {
        postService.getReplies(postId).onEach { result ->
            repliesState = when (result) {
                is Resource.Success -> {
                    RepliesState(replies = result.data.descendants)
                }

                is Resource.Error -> {
                    RepliesState(error = result.message)
                }

                is Resource.Loading -> {
                    RepliesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createReply(postId: String, commentText: String) {
        if (commentText.isNotEmpty()) {
            postService.createReply(postId, commentText).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        ownReplyState = OwnReplyState(reply = result.data)
                        repliesState =
                            repliesState.copy(replies = repliesState.replies + result.data)
                    }

                    is Resource.Error -> {
                        ownReplyState =
                            OwnReplyState(error = result.message)
                    }

                    is Resource.Loading -> {
                        ownReplyState = OwnReplyState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteReply(postId: String) {
        postEditorService.deletePost(postId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    repliesState =
                        repliesState.copy(replies = repliesState.replies.filter { it.id != postId })
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


    fun loadLikedBy(postId: String) {
        accountService.getLikedBy(postId).onEach { result ->
            likedByState = when (result) {
                is Resource.Success -> {
                    LikedByState(likedBy = result.data)
                }

                is Resource.Error -> {
                    LikedByState(error = result.message)
                }

                is Resource.Loading -> {
                    LikedByState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun likePost(postId: String, updatePost: (Post) -> Unit) {
        if (post?.favourited == false) {
            post = post?.copy(
                favourited = true,
                favouritesCount = post!!.favouritesCount + 1,
                likedBy = post!!.likedBy?.copy(
                    totalCount = post!!.likedBy!!.totalCount + 1,
                    others = true,
                    username = post!!.likedBy!!.username ?: myUsername,
                    id = post?.likedBy?.id ?: myAccountId
                ) ?: LikedBy(
                    totalCount = 1, others = true, username = myUsername, id = myAccountId
                )
            )
            post?.let { updatePost(it) }
            CoroutineScope(Dispatchers.Default).launch {
                postService.likePost(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(
                                favourited = result.data.favourited,
                                favouritesCount = result.data.favouritesCount,
                            )
                            post?.let { updatePost(it) }
                        }

                        is Resource.Error -> {
                            post = post?.copy(
                                favourited = false,
                                favouritesCount = result.data?.favouritesCount?.minus(1) ?: 0
                            )
                            post?.let { updatePost(it) }
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun unlikePost(postId: String, updatePost: (Post) -> Unit) {
        if (!post!!.favourited) {
            return
        }
        post = post?.copy(
            favourited = false,
            favouritesCount = post?.favouritesCount?.minus(
                1
            ) ?: 0,
        )

        post?.likedBy?.let {
            if (it.username == myUsername) {
                post = post!!.copy(
                    likedBy = post!!.likedBy!!.copy(
                        username = null,
                        totalCount = post!!.likedBy!!.totalCount - 1
                    )
                )
            } else {
                post = post!!.copy(
                    likedBy = post!!.likedBy!!.copy(totalCount = post!!.likedBy!!.totalCount - 1)
                )
            }
        }

        post?.let { updatePost(it) }

        CoroutineScope(Dispatchers.Default).launch {
            postService.unlikePost(postId).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        post = post?.copy(favourited = result.data.favourited)
                        post?.let { updatePost(it) }
                    }

                    is Resource.Error -> {
                        post = post?.copy(
                            favourited = true,
                            favouritesCount = result.data?.favouritesCount?.plus(1) ?: 0
                        )
                        post?.let { updatePost(it) }
                    }

                    is Resource.Loading -> {
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun reblogPost(postId: String, updatePost: (Post) -> Unit) {
        if (post?.reblogged == false) {
            post = post?.copy(
                reblogged = true,
                reblogCount = post?.reblogCount?.plus(1) ?: 0
            )
            post?.let { updatePost(it) }
            CoroutineScope(Dispatchers.Default).launch {
                postService.reblogPost(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(reblogged = result.data.reblogged, reblogCount = result.data.reblogCount)
                            post?.let { updatePost(it) }
                        }

                        is Resource.Error -> {
                            post = post?.copy(
                                reblogged = false,
                                reblogCount = post?.reblogCount?.minus(1) ?: 0
                            )
                            post?.let { updatePost(it) }
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun unreblogPost(postId: String, updatePost: (Post) -> Unit) {
        if (post?.reblogged == true) {
            post = post?.copy(reblogged = false, reblogCount = post?.reblogCount?.minus(1) ?: 0)
            post?.let { updatePost(it) }
            CoroutineScope(Dispatchers.Default).launch {
                postService.unreblogPost(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(
                                reblogged = result.data.reblogged,
                                reblogCount = result.data.reblogCount
                            )
                            post?.let { updatePost(it) }
                        }

                        is Resource.Error -> {
                            post = post?.copy(
                                reblogged = true,
                                reblogCount = post?.reblogCount?.plus(1) ?: 0
                            )
                            post?.let { updatePost(it) }
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun bookmarkPost(postId: String, updatePost: (Post) -> Unit) {
        if (post?.bookmarked == false) {
            post = post?.copy(bookmarked = true)
            post?.let { updatePost(it) }
            CoroutineScope(Dispatchers.Default).launch {
                postService.bookmarkPost(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(bookmarked = result.data.bookmarked)
                            post?.let { updatePost(it) }
                        }

                        is Resource.Error -> {
                            post = post?.copy(bookmarked = false)
                            post?.let { updatePost(it) }
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun unBookmarkPost(postId: String, updatePost: (Post) -> Unit) {
        if (post?.bookmarked == true) {
            post = post?.copy(bookmarked = false)
            post?.let { updatePost(it) }
            CoroutineScope(Dispatchers.Default).launch {
                postService.unBookmarkPost(postId).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            post = post?.copy(bookmarked = result.data.bookmarked)
                            post?.let { updatePost(it) }
                        }

                        is Resource.Error -> {
                            post = post?.copy(bookmarked = true)
                            post?.let { updatePost(it) }
                        }

                        is Resource.Loading -> {
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun reportPost(category: String) {
        reportState = ReportState(isLoading = true, reported = false)
        if (post == null) {
            reportState = ReportState(
                isLoading = false,
                reported = false,
                error = "an unexpected error occurred"
            )
            return
        }
        val newReport = NewReport(
            reportType = category,
            objectType = ReportObjectType.POST,
            objectId = post!!.id
        )
        CoroutineScope(Dispatchers.Default).launch {
            postService.reportPost(newReport).onEach { result ->
                reportState = when (result) {
                    is Resource.Success -> {
                        ReportState(
                            reported = true
                        )
                    }

                    is Resource.Error -> {
                        ReportState(
                            error = "an unexpected error occured"
                        )
                    }

                    is Resource.Loading -> {
                        ReportState(
                            isLoading = true
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun muteAccount(userId: String) {
        accountService.muteAccount(userId).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message)
                }

                is Resource.Loading -> {
                    RelationshipState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun blockAccount(userId: String) {
        accountService.blockAccount(userId).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message)
                }

                is Resource.Loading -> {
                    RelationshipState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun openUrl(url: String) {
        platform.openUrl(url)
    }

    fun saveImage(url: String) {
        viewModelScope.launch {
            fileService.download(url)
        }
    }

    fun shareText(text: String) {
        platform.shareText(text)
    }

    fun updateReplyText(newReplyText: TextFieldValue) {
        replyText = newReplyText
        hashtagMentionsSuggestionsManager.changeText(newReplyText, viewModelScope)
    }
}