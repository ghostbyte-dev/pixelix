package com.daniebeler.pfpixelix.domain.model

data class LikedBy(
    val id: String?,
    val username: String?,
    val others: Boolean,
    val totalCount: Int
)