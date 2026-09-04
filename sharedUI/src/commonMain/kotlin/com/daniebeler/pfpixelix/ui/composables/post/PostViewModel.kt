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
import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.ReportObjectType
import com.daniebeler.pfpixelix.domain.model.request.UserBlockRequest
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.InstanceService
import com.daniebeler.pfpixelix.domain.service.general.PostEditorService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.general.ReplyChildrenState
import com.daniebeler.pfpixelix.domain.service.general.ReplyNode
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.general.insertChild
import com.daniebeler.pfpixelix.domain.service.general.removeNode
import com.daniebeler.pfpixelix.domain.service.general.updateChildrenState
import com.daniebeler.pfpixelix.domain.service.general.updatePost
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.suggestions.HashtagMentionsSuggestionsManager
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.post.reply.OwnReplyState
import com.daniebeler.pfpixelix.ui.composables.post.reply.RepliesState
import com.daniebeler.pfpixelix.ui.composables.profile.RelationshipState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject


class PostViewModel @Inject constructor(
    private val postService: PostService,
    private val prefs: UserPreferences,
    private val postEditorService: PostEditorService,
    authService: AuthService,
    private val accountService: AccountService,
    private val platform: Platform,
    private val fileService: FileService,
    private val instanceService: InstanceService,
    session: Session,
    val hashtagMentionsSuggestionsManager: HashtagMentionsSuggestionsManager
) : ViewModel() {
    val capabilities = session.capabilities
    var post: Post? by mutableStateOf(null)

    var repliesState by mutableStateOf(RepliesState())

    var ownReplyState by mutableStateOf(OwnReplyState())

    var likedByState by mutableStateOf(LikedByState())

    private val _deleteEventChannel = Channel<DeleteEvent>()
    val deleteEvents = _deleteEventChannel.receiveAsFlow()
    var deleteState by mutableStateOf(DeleteState())
    var deleteDialog: String? by mutableStateOf(null)
    var reportState by mutableStateOf<ReportState?>(null)
    var showPost: Boolean by mutableStateOf(false)

    var myAccountId: String? = null
    var myUsername: String? = null

    var isAltTextButtonHidden by mutableStateOf(false)
    var hideMetadataPref by mutableStateOf(true)
    var isDoubleTapEnabled by mutableStateOf(true)
    var isAutoplayVideos by mutableStateOf(true)
    var blurSensitiveContent by mutableStateOf(false)
    var instance: Instance? = null
    var replyText by mutableStateOf(TextFieldValue())

    var volume by mutableStateOf(prefs.enableVolume)
    var relationshipState by mutableStateOf(RelationshipState())

    val mutedAccount: MutedAccount?
        get() {
            val account = post?.account ?: return null
            val relationship = relationshipState.accountRelationship ?: return null
            return MutedAccount(
                id = account.id, account = account, muteOptions = UserMuteRequest(
                    mute = relationship.muted,
                    muteNotifications = relationship.mutedNotifications,
                    muteReblogs = relationship.mutedReblogs,
                    muteStatuses = relationship.mutedStatuses
                )
            )
        }

    init {
        myAccountId = authService.getCurrentSession()!!.accountId
        myUsername = authService.getCurrentSession()!!.username
        viewModelScope.launch {
            prefs.hideAltTextButtonFlow.collect {
                isAltTextButtonHidden = it
            }
        }
        viewModelScope.launch {
            prefs.hideMetadataFlow.collect { hideMetadataPref = it }
        }
        viewModelScope.launch {
            prefs.enableDoubleTapToLikeFlow.collect { isDoubleTapEnabled = it }
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

    fun getInstance() {
        if (instance != null) {
            return
        }
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
            when (result) {
                is Resource.Success -> {
                    deleteState = DeleteState()
                    _deleteEventChannel.send(DeleteEvent.Success)
                }

                is Resource.Error -> {
                    deleteState = DeleteState(error = result.message)
                }

                is Resource.Loading -> {
                    deleteState = DeleteState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleShowPost() {
        showPost = !showPost
    }

    fun loadRepliesInit(postId: String) {
        repliesState = repliesState.copy(isLoading = true)
        postService.getReplies(postId).onEach { result ->
            repliesState = when (result) {
                is Resource.Success -> repliesState.copy(replies = result.data, isLoading = false)
                is Resource.Error -> repliesState.copy(error = result.message, isLoading = false)
                is Resource.Loading -> repliesState
            }
        }.launchIn(viewModelScope)
    }

    fun loadReplies(postId: String) {
        repliesState = repliesState.copy(
            replies = repliesState.replies.updateChildrenState(postId) { ReplyChildrenState.Loading }
        )

        postService.getReplies(postId).onEach { result ->
            repliesState = repliesState.copy(
                replies = repliesState.replies.updateChildrenState(postId) { current ->
                    when (result) {
                        is Resource.Success -> ReplyChildrenState.Loaded(result.data)
                        is Resource.Error -> ReplyChildrenState.Error(result.message)
                        is Resource.Loading -> current
                    }
                }
            )
        }.launchIn(viewModelScope)
    }

    fun createReply(postId: String, commentText: String) {
        if (commentText.isNotEmpty()) {
            postService.createReply(postId, commentText).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        ownReplyState = OwnReplyState(reply = result.data)

                        val newNode = ReplyNode(
                            post = result.data,
                            knownReplyCount = 0,
                            childrenState = ReplyChildrenState.Loaded(emptyList())
                        )

                        repliesState = if (postId == post?.id) {
                            repliesState.copy(replies = repliesState.replies + newNode)
                        } else {
                            repliesState.copy(
                                replies = repliesState.replies.insertChild(
                                    postId,
                                    newNode
                                )
                            )
                        }
                    }

                    is Resource.Error -> {
                        ownReplyState = OwnReplyState(error = result.message)
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
                    repliesState = repliesState.copy(
                        replies = repliesState.replies.removeNode(postId)
                    )
                }

                is Resource.Error -> {
                    repliesState = repliesState.copy(error = result.message)
                }

                is Resource.Loading -> {
                    Logger.v("is loading")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun likeReply(postId: String) {
        repliesState = repliesState.copy(
            replies = repliesState.replies.updatePost(postId) { it.copy(favourited = true) }
        )

        postService.likePost(postId).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    repliesState = repliesState.copy(
                        replies = repliesState.replies.updatePost(postId) { it.copy(favourited = false) }
                    )
                }

                is Resource.Success -> { /* already applied optimistically */
                }

                is Resource.Loading -> Logger.v("is loading")
            }
        }.launchIn(viewModelScope)
    }

    fun unlikeReply(postId: String) {
        repliesState = repliesState.copy(
            replies = repliesState.replies.updatePost(postId) { it.copy(favourited = false) }
        )

        postService.unlikePost(postId).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    repliesState = repliesState.copy(
                        replies = repliesState.replies.updatePost(postId) { it.copy(favourited = true) }
                    )
                }

                is Resource.Success -> {}
                is Resource.Loading -> Logger.v("is loading")
            }
        }.launchIn(viewModelScope)
    }

    fun getRelationship() {
        if (post?.account?.username != null) {
            accountService.getRelationships(List(1) { post!!.account.id }).onEach { result ->
                relationshipState = when (result) {
                    is Resource.Success -> {
                        RelationshipState(
                            accountRelationship = if (result.data.isNotEmpty()) {
                                result.data[0]
                            } else {
                                null
                            }
                        )
                    }

                    is Resource.Error -> {
                        RelationshipState(error = result.message)
                    }

                    is Resource.Loading -> {
                        RelationshipState(
                            isLoading = true,
                            accountRelationship = relationshipState.accountRelationship
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }


    fun loadLikedBy(postId: String) {
        postService.getLikedBy(postId).onEach { result ->
            likedByState = when (result) {
                is Resource.Success -> {
                    LikedByState(likedBy = result.data.data)
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
            post = if (it.username == myUsername) {
                post!!.copy(
                    likedBy = post!!.likedBy!!.copy(
                        username = null, totalCount = post!!.likedBy!!.totalCount - 1
                    )
                )
            } else {
                post!!.copy(
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
                reblogged = true, reblogCount = post?.reblogCount?.plus(1) ?: 0
            )
            post?.let { updatePost(it) }
            CoroutineScope(Dispatchers.Default).launch {
                postService.reblogPost(postId).onEach { result ->
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
                                reblogged = false, reblogCount = post?.reblogCount?.minus(1) ?: 0
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
                                reblogged = true, reblogCount = post?.reblogCount?.plus(1) ?: 0
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
                isLoading = false, reported = false, error = "an unexpected error occurred"
            )
            return
        }
        val newReport = NewReport(
            reportType = category, objectType = ReportObjectType.POST, objectId = post!!.id
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

    fun muteAccount(userId: String, username: String, userMuteRequest: UserMuteRequest) {
        accountService.muteAccount(userId, username, userMuteRequest).onEach { result ->
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

    fun blockAccount(userId: String, username: String, userBlockRequest: UserBlockRequest) {
        accountService.blockAccount(userId, username, userBlockRequest).onEach { result ->
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

