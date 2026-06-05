package com.daniebeler.pfpixelix.ui.composables.collection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.CollectionService
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class CollectionViewModel @Inject constructor(
    private val platform: Platform,
    private val collectionService: CollectionService,
    private val postService: PostService,
    private val authService: AuthService,
    private val prefs: UserPreferences,
) : ViewModel() {

    var collectionState by mutableStateOf(CollectionState())
    var collectionPostsState by mutableStateOf(CollectionPostsState())
    var editState by mutableStateOf(EditCollectionState())
    var myUsername: String? = null
    var page: Int = 1
    var view by mutableStateOf(ViewEnum.Grid)

    init {
        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = ViewEnum.getView(res)
            }
        }
    }

    fun loadData(collectionId: String) {
        if (collectionState.id == null) {
            myUsername = authService.getCurrentSession()!!.username
            collectionState = collectionState.copy(id = collectionId, isLoading = true)
            getCollection()
            getPostsFirstLoad(false)
        }
    }

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView.ordinal
    }

    fun getCollection() {
        if (collectionState.id != null) {
            collectionService.getCollection(collectionState.id!!).onEach { result ->
                collectionState = when (result) {
                    is Resource.Success -> {
                        CollectionState(
                            collection = result.data, id = collectionState.id
                        )
                    }

                    is Resource.Error -> {
                        CollectionState(
                            error = result.message ?: "An unexpected error occurred",
                            id = collectionState.id
                        )
                    }

                    is Resource.Loading -> {
                        CollectionState(
                            isLoading = true,
                            collection = collectionState.collection,
                            id = collectionState.id
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun getPostsFirstLoad(refreshing: Boolean) {
        if (collectionState.id != null) {
            collectionService.getPostsOfCollection(collectionState.id!!, 1).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val endReached = (result.data.size) < 10
                        collectionPostsState = CollectionPostsState(
                            posts = result.data, endReached = endReached
                        )
                        getPostsPaginated(false)
                    }

                    is Resource.Error -> {
                        collectionPostsState = CollectionPostsState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }

                    is Resource.Loading -> {
                        collectionPostsState = CollectionPostsState(
                            isLoading = true,
                            isRefreshing = refreshing,
                            posts = collectionPostsState.posts
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getPostsPaginated(refreshing: Boolean) {
        if (collectionPostsState.endReached) return
        if (collectionState.id != null) {
            if (collectionPostsState.posts.isEmpty()) {
                return
            }
            page += 1

            collectionService.getPostsOfCollection(
                collectionState.id!!,
                page
            ).onEach { result ->
                collectionPostsState = when (result) {
                    is Resource.Success -> {
                        val endReached = (result.data.size) < 10
                        var newPosts: List<Post> = result.data
                        newPosts = newPosts.drop(1);
                        CollectionPostsState(
                            posts = collectionPostsState.posts + newPosts,
                            endReached = endReached
                        )
                    }

                    is Resource.Error -> {
                        CollectionPostsState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }

                    is Resource.Loading -> {
                        CollectionPostsState(
                            isLoading = true,
                            isRefreshing = refreshing,
                            posts = collectionPostsState.posts
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getAllPosts() {
        if (editState.allPosts.isNotEmpty()) {
            return
        }
        postService.getOwnPosts().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val endReached = (result.data.size) < PixelfedApi.PROFILE_POSTS_LIMIT

                    editState = editState.copy(
                        allPosts = result.data,
                        isAllPostsEndReached = endReached,
                        isAllPostsLoading = false
                    )
                }

                is Resource.Error -> {
                    editState = editState.copy(errorAllPosts = "An unexpected error occurred", isAllPostsLoading = false)
                }

                is Resource.Loading -> {
                    editState = editState.copy(isAllPostsLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getPostsExceptCollectionPaginated() {
        if (!editState.isAllPostsLoading && editState.allPosts.isNotEmpty() && !editState.isAllPostsEndReached) {
            postService.getOwnPosts(editState.allPosts.last().id).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val endReached = (result.data.size) < PixelfedApi.PROFILE_POSTS_LIMIT

                        editState = editState.copy(
                            allPosts = editState.allPosts + result.data,
                            isAllPostsEndReached = endReached,
                            isAllPostsLoading = false,
                            errorAllPosts = ""
                        )
                    }

                    is Resource.Error -> {
                        editState = editState.copy(error = "An unexpected error occurred", isAllPostsLoading = false)
                    }

                    is Resource.Loading -> {
                        editState = editState.copy(isAllPostsLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addPostToCollection(id: String) {
        val postToAdd = editState.allPosts.find { it.id == id }
        postToAdd?.let {
            val posts = editState.editPosts + postToAdd
            editState = editState.copy(
                editPosts = posts,
                addedIds = editState.addedIds + id,
            )
        }
    }

    fun confirmEdit() {
        collectionPostsState = collectionPostsState.copy(posts = editState.editPosts)
        editState = editState.copy(editMode = false)
        editState.removedIds.forEach {
            removePostOfCollection(it)
        }
        editState.addedIds.forEach {
            addPostsOfCollection(
                it
            )
        }
        if (editState.name != collectionState.collection!!.title) {
            updateCollection(editState.name)
        }
        collectionState =
            collectionState.copy(collection = collectionState.collection!!.copy(title = editState.name))
    }

    private fun updateCollection(newName: String) {
        if (collectionState.id != null && collectionState.collection != null) {
            collectionService.updateCollection(
                collectionState.id!!,
                newName,
                collectionState.collection!!.description,
                collectionState.collection!!.visibility
            ).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        getCollection()
                    }

                    is Resource.Error -> {
                        editState = editState.copy(updateError = "An unexpected error occurred while updating the collection")
                    }

                    is Resource.Loading -> {

                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun addPostsOfCollection(postId: String) {
        if (collectionState.id != null) {
            collectionService.addPostOfCollection(collectionState.id!!, postId).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        //getPostsFirstLoad(false)
                    }

                    is Resource.Error -> {
                        editState = editState.copy(updateError = "An unexpected error occurred while updating the collection")
                    }

                    is Resource.Loading -> {

                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun removePostOfCollection(postId: String) {
        if (collectionState.id != null) {
            collectionService.removePostOfCollection(collectionState.id!!, postId)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            //getPostsFirstLoad(false)
                        }

                        is Resource.Error -> {
                            editState = editState.copy(updateError = "An unexpected error occurred while updating the collection")
                        }

                        is Resource.Loading -> {

                        }
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun toggleEditMode() {
        val newEditState = editState.copy()
        if (!editState.editMode) {
            newEditState.addedIds = emptyList()
            newEditState.removedIds = emptyList()
            newEditState.editPosts = collectionPostsState.posts
            newEditState.name = collectionState.collection?.title ?: ""
        }
        newEditState.editMode = !newEditState.editMode
        editState = newEditState
    }

    fun editRemove(id: String) {
        val newEditState = editState.copy()
        newEditState.removedIds += id
        newEditState.editPosts =
            newEditState.editPosts.filter { !newEditState.removedIds.contains(it.id) }
        newEditState.addedIds = newEditState.addedIds.filter { it != id }
        editState = newEditState
    }

    fun refresh() {
        page = 1
        getPostsFirstLoad(true)
    }

    fun openUrl(url: String) {
        platform.openUrl(url)
    }

    fun shareCollectionUrl() {
        collectionState.collection?.url?.let { platform.shareText(it) }
    }

    fun filterPostsExceptCollection(posts: List<Post>): List<Post> {
        val excludedIds = editState.editPosts.map { it.id }.toSet()
        return posts.filter { post -> post.id !in excludedIds }
    }
}