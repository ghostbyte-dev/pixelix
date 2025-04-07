package com.daniebeler.pfpixelix.ui.composables.profile.other_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.account.AccountService
import com.daniebeler.pfpixelix.domain.service.collection.CollectionService
import com.daniebeler.pfpixelix.domain.service.hashtag.SearchService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.post.PostService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
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
    private val searchService: SearchService,
    private val platform: Platform,
    private val prefs: UserPreferences,
    private val collectionService: CollectionService,
    private val authService: AuthService
) : ViewModel() {
    var userId: String = ""
    var accountState by mutableStateOf(AccountState())
    var relationshipState by mutableStateOf(RelationshipState())
    var mutualFollowersState by mutableStateOf(MutualFollowersState())
    var postsState by mutableStateOf(PostsState())
    private var collectionPage by mutableIntStateOf(1)
    var collectionsState by mutableStateOf(CollectionsState())

    var domain by mutableStateOf("")
    var view by mutableStateOf(ViewEnum.Grid)

    fun loadData(_userId: String, refreshing: Boolean, navController: NavController) {
        val myAccountId = authService.getCurrentSession()!!.accountId

        if (_userId == myAccountId) {
            navController.popBackStack()
            navController.navigate(Destination.OwnProfile)
        }

        userId = _userId
        getAccount(userId, refreshing)
        loadDataExceptAccount(refreshing)

    }

    private fun loadDataExceptAccount(refreshing: Boolean) {
        getPostsFirstLoad(userId, refreshing)

        getRelationship(userId)

        getMutualFollowers(userId)
        getCollections(userId, false)

        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = if (res) ViewEnum.Grid else ViewEnum.Timeline
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

    private fun getRelationship(userId: String) {
        searchService.getRelationships(List(1) { userId }).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(
                        accountRelationship = if (!result.data.isNullOrEmpty()) {
                            result.data[0]
                        } else {
                            null
                        }
                    )
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
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
                    MutualFollowersState(mutualFollowers = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    MutualFollowersState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    MutualFollowersState(
                        isLoading = true, mutualFollowers = mutualFollowersState.mutualFollowers
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAccount(userId: String, refreshing: Boolean) {
        accountService.getAccount(userId).onEach { result ->
            accountState = when (result) {
                is Resource.Success -> {
                    AccountState(account = result.data)
                }

                is Resource.Error -> {
                    AccountState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    AccountState(
                        isLoading = true,
                        account = accountState.account,
                        refreshing = refreshing
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
                    userId = result.data!!.id
                    loadDataExceptAccount(refreshing)
                    AccountState(account = result.data)
                }

                is Resource.Error -> {
                    AccountState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    AccountState(
                        isLoading = true,
                        account = accountState.account,
                        refreshing = refreshing
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
                        CollectionsState(collections = result.data ?: emptyList())
                    } else {
                        val endReached = result.data!!.isEmpty()
                        CollectionsState(
                            collections = collectionsState.collections + result.data,
                            endReached = endReached
                        )
                    }
                }

                is Resource.Error -> {
                    collectionsState =
                        CollectionsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    collectionsState = CollectionsState(
                        isLoading = true, collections = collectionsState.collections
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getPostsFirstLoad(userId: String, refreshing: Boolean) {
        if (postsState.posts.isNotEmpty() && !refreshing) {
            return
        }
        postService.getPostsOfAccount(userId).onEach { result ->
            postsState = when (result) {
                is Resource.Success -> {
                    val endReached = (result.data.posts.size) < PixelfedApi.PROFILE_POSTS_LIMIT
                    PostsState(posts = result.data.posts, endReached = endReached, nextCursor = result.data.cursor)
                }

                is Resource.Error -> {
                    PostsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    PostsState(isLoading = true, posts = postsState.posts, refreshing = refreshing)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getPostsPaginated(userId: String) {
        if (postsState.posts.isNotEmpty() && !postsState.isLoading && !postsState.endReached) {
            postService.getPostsOfAccount(userId, postsState.nextCursor).onEach { result ->
                postsState = when (result) {
                    is Resource.Success -> {
                        val endReached = (result.data.posts.size ?: 0) < PixelfedApi.PROFILE_POSTS_LIMIT
                        PostsState(
                            posts = postsState.posts + (result.data.posts ?: emptyList()),
                            endReached = endReached,
                            nextCursor = result.data.cursor
                        )
                    }

                    is Resource.Error -> {
                        PostsState(error = result.message ?: "An unexpected error occurred")
                    }

                    is Resource.Loading -> {
                        PostsState(isLoading = true, posts = postsState.posts)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun followAccount(userId: String) {
        accountService.followAccount(userId).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
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

    fun unfollowAccount(userId: String) {
        accountService.unfollowAccount(userId).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
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

    fun muteAccount(userId: String) {
        accountService.muteAccount(userId).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    RelationshipState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unMuteAccount(userId: String) {
        accountService.unMuteAccount(userId).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
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
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    RelationshipState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unblockAccount(userId: String) {
        accountService.unblockAccount(userId).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
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

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView == ViewEnum.Grid
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