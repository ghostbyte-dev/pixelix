package com.daniebeler.pfpixelix.ui.composables.collection

import com.daniebeler.pfpixelix.domain.model.Post

data class EditCollectionState(
    val isLoading: Boolean = false,
    val isAllPostsLoading: Boolean = false,
    var allPosts: List<Post> = emptyList(),
    val nextId: String? = null,
    var editMode: Boolean = false,
    var editPosts: List<Post> = emptyList(),
    var removedIds: List<String> = emptyList(),
    var addedIds: List<String> = emptyList(),
    var name: String = "",
    val error: String = "",
    val updateError: String = "",
    val errorAllPosts: String = "",
    val isAllPostsEndReached: Boolean = false
)