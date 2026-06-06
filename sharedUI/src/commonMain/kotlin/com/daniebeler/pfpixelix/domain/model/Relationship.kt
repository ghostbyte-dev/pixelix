package com.daniebeler.pfpixelix.domain.model

data class Relationship(
    val id: String,
    val following: Boolean,
    val followedBy: Boolean,
    val muting: Boolean,
    val blocking: Boolean
)