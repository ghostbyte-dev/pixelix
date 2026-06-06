package com.daniebeler.pfpixelix.domain.model

data class Chat(
    val avatar: String,
    val id: String,
    val isLocal: Boolean,
    var messages: List<Message>,
    val muted: Boolean,
    val name: String,
    val timeAgo: String,
    val url: String,
    val username: String
)