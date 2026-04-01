package com.daniebeler.pfpixelix.ui.composables.collection

import com.daniebeler.pfpixelix.domain.model.Post

data class CollectionPostsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String = ""
)