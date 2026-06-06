package com.daniebeler.pfpixelix.domain.model

data class NewPost(
    val status: String,
    val mediaIds: List<String>,
    val sensitive: Boolean,
    val visibility: Visibility,
    val spoilerText: String?,
    val placeId: String?
)