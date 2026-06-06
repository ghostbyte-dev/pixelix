package com.daniebeler.pfpixelix.domain.model

data class Conversation(
    val id: Int,
    val unread: Boolean,
    val accounts: List<Account>,
    val lastPost: Post
)