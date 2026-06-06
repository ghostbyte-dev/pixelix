package com.daniebeler.pfpixelix.domain.model

data class Notification(
    val account: Account = Account(),
    val id: String,
    val type: String,
    val post: Post?,
    val createdAt: String
)