package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedChatDto(
    @SerialName("avatar") val avatar: String,
    @SerialName("id") val id: String,
    @SerialName("isLocal") val isLocal: Boolean,
    @SerialName("messages") val messages: List<PixelfedMessageDto>,
    @SerialName("muted") val muted: Boolean,
    @SerialName("name") val name: String,
    @SerialName("timeAgo") val timeAgo: String,
    @SerialName("url") val url: String,
    @SerialName("username") val username: String
)

// --- MAPPING EXTENSIONS ---

fun PixelfedChatDto.toDomain(): Chat {
    return Chat(
        avatar = this.avatar,
        id = this.id,
        isLocal = this.isLocal,
        messages = this.messages.map { it.toDomain() },
        muted = this.muted,
        name = this.name,
        timeAgo = this.timeAgo,
        url = this.url,
        username = this.username
    )
}

fun Chat.toDto(): PixelfedChatDto {
    return PixelfedChatDto(
        avatar = this.avatar,
        id = this.id,
        isLocal = this.isLocal,
        messages = this.messages.map { it.toDto() },
        muted = this.muted,
        name = this.name,
        timeAgo = this.timeAgo,
        url = this.url,
        username = this.username
    )
}