package com.daniebeler.pfpixelix.domain.model

data class Message(
    val hidden: Boolean,
    val id: String,
    val isAuthor: Boolean,
    val reportId: String,
    val seen: Boolean,
    val text: String = "",
    val timeAgo: String,
    val type: String
)