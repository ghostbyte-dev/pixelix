package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chat (
    @SerialName("avatar") val avatar: String,
    @SerialName("id") val id: String,
    @SerialName("isLocal") val isLocal: Boolean,
    @SerialName("messages") var messages: List<Message>,
    @SerialName("muted") val muted: Boolean,
    @SerialName("name") val name: String,
    @SerialName("timeAgo") val timeAgo: String,
    @SerialName("url") val url: String,
    @SerialName("username") val username: String
)