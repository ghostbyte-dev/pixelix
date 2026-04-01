package com.daniebeler.pfpixelix.domain.model

data class LikedPostsWithNext(
    val posts: List<Post> = emptyList(),
    val nextId: String = ""
)
