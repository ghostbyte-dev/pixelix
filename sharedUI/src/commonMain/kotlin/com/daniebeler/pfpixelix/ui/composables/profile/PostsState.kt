package com.daniebeler.pfpixelix.ui.composables.profile

import com.daniebeler.pfpixelix.domain.model.Post

data class PostsState(
    val isLoading: Boolean = false,
    val refreshing: Boolean = false,
    val endReached: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String = ""
)
