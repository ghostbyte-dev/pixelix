package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Conversation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedConversationDto(
    @SerialName("id") val id: Int,
    @SerialName("unread") val unread: Boolean,
    @SerialName("accounts") val accounts: List<PixelfedAccountDto>,
    @SerialName("last_status") val lastPost: PixelfedPostDto
)

fun PixelfedConversationDto.toDomain(): Conversation {
    return Conversation(
        id = this.id,
        unread = this.unread,
        accounts = this.accounts.map { it.toDomain() },
        lastPost = this.lastPost.toDomain()
    )
}