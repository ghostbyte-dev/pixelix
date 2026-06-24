package com.daniebeler.pfpixelix.domain.model

data class Relationship(
    val id: String,
    val following: Boolean,
    val followedBy: Boolean,
    val blocked: Boolean,
    val muted: Boolean? = null,
    val mutedNotifications: Boolean? = null,
    val mutedReblogs: Boolean? = null,
    val mutedStatuses: Boolean? = null,
    val requested: Boolean,
    val requestedBy: Boolean,
)