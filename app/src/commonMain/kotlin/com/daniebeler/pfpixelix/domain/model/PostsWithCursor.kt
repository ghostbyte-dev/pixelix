package com.daniebeler.pfpixelix.domain.model

data class PostsWithCursor(
    val posts: List<Post> = emptyList(),
    val cursor: String = ""
)
