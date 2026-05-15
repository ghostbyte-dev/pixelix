package com.daniebeler.pfpixelix.domain.model

data class BookmarkedPostsWithNextCursor(
    val posts: List<Post> = emptyList(),
    val cursor: String = ""
)
