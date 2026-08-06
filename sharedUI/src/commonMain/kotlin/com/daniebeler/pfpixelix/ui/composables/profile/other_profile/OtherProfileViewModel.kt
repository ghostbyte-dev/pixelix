package com.daniebeler.pfpixelix.ui.composables.profile.other_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.request.UserBlockRequest
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.general.CollectionService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.notifications.FollowRequestState
import com.daniebeler.pfpixelix.ui.composables.profile.AccountState
import com.daniebeler.pfpixelix.ui.composables.profile.CollectionsState
import com.daniebeler.pfpixelix.ui.composables.profile.MutualFollowersState
import com.daniebeler.pfpixelix.ui.composables.profile.PostsState
import com.daniebeler.pfpixelix.ui.composables.profile.RelationshipState
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import com.daniebeler.pfpixelix.ui.navigation.Destination
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class OtherProfileViewModel(
    private val accountService: AccountService,
    private val postService: PostService,
    private val platform: Platform,
    private val prefs: UserPreferences,
    private val collectionService: CollectionService,
    private val authService: AuthService,
    private val session: Session
) : ViewModel() {
    val capabilities = session.capabilities

    var userId: String = ""
    var username: String = ""
    var accountState by mutableStateOf(AccountState())
    var relationshipState by mutableStateOf(RelationshipState())
    var mutualFollowersState by mutableStateOf(MutualFollowersState())
    var postsState by mutableStateOf(PostsState())
    private var collectionPage by mutableIntStateOf(1)
    var collectionsState by mutableStateOf(CollectionsState())
    var followRequestState by mutableStateOf(FollowRequestState())

    var domain by mutableStateOf("")
    var view by mutableStateOf(ViewEnum.Grid)

    val mutedAccount: MutedAccount?
        get() {
            val account = accountState.account ?: return null
            val relationship = relationshipState.accountRelationship ?: return null
            return MutedAccount(
                id = account.id,
                account = account,
                muteOptions = UserMuteRequest(
                    mute = relationship.muted,
                    muteNotifications = relationship.mutedNotifications,
                    muteReblogs = relationship.mutedReblogs,
                    muteStatuses = relationship.mutedStatuses
                )
            )
        }

    fun loadData(
        userId: String?, username: String?, refreshing: Boolean, navController: NavController
    ) {
        if (username == null) {
            if (session.backendType.value == BackendType.VERNISSAGE) {
                accountState =
                    AccountState(error = "Vernissage requires username for loading profile")
            }

            return
        }
        if (userId == null) {
            loadDataByUsername(username, false, navController)
            return
        }
        val credentials = authService.getCurrentSession()

        val myAccountId = credentials?.accountId
        val myUsername = credentials?.username

        if (userId == myAccountId || userId == myUsername) {
            navController.popBackStack()
            navController.navigate(Destination.OwnProfile)
        }

        this.userId = userId
        this.username = username
        getAccount(userId, username, refreshing)
        loadDataExceptAccount(refreshing)

    }

    private fun loadDataExceptAccount(refreshing: Boolean) {
        getPostsFirstLoad(userId, username, refreshing)

        getRelationship(userId)

        getMutualFollowers(userId)
        getCollections(userId, false)

        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = ViewEnum.getView(res)
            }
        }
    }

    fun loadDataByUsername(username: String, refreshing: Boolean, navController: NavController) {
        val myUsername = authService.getCurrentSession()!!.username
        if (username == myUsername) {
            navController.popBackStack()
            navController.navigate(Destination.OwnProfile)
        }
        getAccountByUsername(username, refreshing)
    }

    fun getRelationship(userId: String) {
        accountService.getRelationships(List(1) { userId }).onEach { result ->
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

    private fun getMutualFollowers(userId: String) {
        accountService.getMutualFollowers(userId).onEach { result ->
            mutualFollowersState = when (result) {
                is Resource.Success -> {
                    MutualFollowersState(mutualFollowers = result.data)
                }

                is Resource.Error -> {
                    MutualFollowersState(error = result.message)
                }

                is Resource.Loading -> {
                    MutualFollowersState(
                        isLoading = true, mutualFollowers = mutualFollowersState.mutualFollowers
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAccount(userId: String, username: String, refreshing: Boolean) {
        accountService.getAccount(userId, username).onEach { result ->
            accountState = when (result) {
                is Resource.Success -> {
                    AccountState(account = result.data)
                }

                is Resource.Error -> {
                    AccountState(error = result.message)
                }

                is Resource.Loading -> {
                    AccountState(
                        isLoading = true, account = accountState.account, refreshing = refreshing
                    )
                }
            }

            if (accountState.account != null) {
                domain = accountState.account?.url?.substringAfter("https://")?.substringBefore("/")
                    ?: ""
            }
        }.launchIn(viewModelScope)
    }

    private fun getAccountByUsername(username: String, refreshing: Boolean) {
        accountService.getAccountByUsername(username).onEach { result ->
            accountState = when (result) {
                is Resource.Success -> {
                    this.username = result.data.username
                    userId = result.data.id
                    loadDataExceptAccount(refreshing)
                    AccountState(account = result.data)
                }

                is Resource.Error -> {
                    AccountState(error = result.message)
                }

                is Resource.Loading -> {
                    AccountState(
                        isLoading = true, account = accountState.account, refreshing = refreshing
                    )
                }
            }

            if (accountState.account != null) {
                domain = accountState.account?.url?.substringAfter("https://")?.substringBefore("/")
                    ?: ""
            }
        }.launchIn(viewModelScope)
    }

    fun getCollections(userId: String, paginated: Boolean) {
        if (collectionsState.endReached) {
            return
        }
        if (!paginated) {
            collectionPage = 1
        } else {
            collectionPage++
        }
        collectionService.getCollections(userId, collectionPage).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    collectionsState = if (!paginated) {
                        CollectionsState(collections = result.data)
                    } else {
                        val endReached = result.data.isEmpty()
                        CollectionsState(
                            collections = collectionsState.collections + result.data,
                            endReached = endReached
                        )
                    }
                }

                is Resource.Error -> {
                    collectionsState =
                        CollectionsState(error = result.message)
                }

                is Resource.Loading -> {
                    collectionsState = CollectionsState(
                        isLoading = true, collections = collectionsState.collections
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getPostsFirstLoad(userId: String, username: String, refreshing: Boolean) {
        if (postsState.posts.isNotEmpty() && !refreshing) {
            return
        }
        postService.getPostsOfAccount(userId, username).onEach { result ->
            postsState = when (result) {
                is Resource.Success -> {
                    val endReached = (result.data.data.size) < PixelfedApi.PROFILE_POSTS_LIMIT
                    PostsState(
                        posts = result.data.data, endReached = endReached, nextId = result.data.next
                    )
                }

                is Resource.Error -> {
                    PostsState(error = result.message)
                }

                is Resource.Loading -> {
                    PostsState(
                        isLoading = true,
                        posts = postsState.posts,
                        refreshing = refreshing,
                        nextId = postsState.nextId
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getPostsPaginated(userId: String) {
        if (postsState.posts.isNotEmpty() && !postsState.isLoading && !postsState.endReached) {
            postService.getPostsOfAccount(userId, username, postsState.nextId).onEach { result ->
                postsState = when (result) {
                    is Resource.Success -> {
                        val endReached = (result.data.data.size) < PixelfedApi.PROFILE_POSTS_LIMIT
                        PostsState(
                            posts = postsState.posts + (result.data.data),
                            endReached = endReached,
                            nextId = result.data.next
                        )
                    }

                    is Resource.Error -> {
                        PostsState(error = result.message)
                    }

                    is Resource.Loading -> {
                        PostsState(
                            isLoading = true, posts = postsState.posts, nextId = postsState.nextId
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun followAccount() {
        accountService.followAccount(userId, username).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
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

    fun unfollowAccount() {
        accountService.unfollowAccount(userId, username).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
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

    fun muteAccount(userMuteRequest: UserMuteRequest) {
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


    fun blockAccount(userBlockRequest: UserBlockRequest) {
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

    fun unblockAccount() {
        accountService.unblockAccount(userId, username).onEach { result ->
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

    fun acceptFollowRequest() {
        val accountId = accountState.account?.id
        if (accountId == null) {
            followRequestState = FollowRequestState(error = "Invalid account")
            return
        }
        accountService.acceptFollowRequest(accountId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    relationshipState = RelationshipState(accountRelationship = result.data)
                    followRequestState = FollowRequestState(relationship = result.data)
                }

                is Resource.Error -> {
                    followRequestState = FollowRequestState(error = result.message)
                }

                is Resource.Loading -> {
                    followRequestState = FollowRequestState(isLoading = true, isAccepting = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun rejectFollowRequest() {
        val accountId = accountState.account?.id
        if (accountId == null) {
            followRequestState = FollowRequestState(error = "Invalid account")
            return
        }
        accountService.rejectFollowRequest(accountId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    relationshipState = RelationshipState(accountRelationship = result.data)
                    followRequestState = FollowRequestState(relationship = result.data)
                }

                is Resource.Error -> {
                    followRequestState = FollowRequestState(error = result.message)
                }

                is Resource.Loading -> {
                    followRequestState = FollowRequestState(isLoading = true, isAccepting = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun openUrl(url: String) {
        platform.openUrl(url)
    }

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView.ordinal
    }

    fun postGetsDeleted(postId: String) {
        postsState = postsState.copy(posts = postsState.posts.filter { post -> post.id != postId })
    }

    fun updatePost(post: Post) {
        postsState = postsState.copy(posts = postsState.posts.map {
            if (it.id == post.id) {
                post
            } else {
                it
            }
        })
    }

    fun shareAccountUrl() {
        accountState.account?.url?.let { platform.shareText(it) }
    }
}