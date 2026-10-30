package com.daniebeler.pfpixelix.domain.model

data class PostsForPlace(
    val place: Place,
    val posts: List<Post>
)