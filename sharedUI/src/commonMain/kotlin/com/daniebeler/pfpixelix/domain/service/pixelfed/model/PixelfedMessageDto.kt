package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedMessageDto(
    @SerialName("hidden") val hidden: Boolean,
    @SerialName("id") val id: String,
    @SerialName("isAuthor") val isAuthor: Boolean,
    @SerialName("reportId") val reportId: String,
    @SerialName("seen") val seen: Boolean,
    @SerialName("text") val text: String = "",
    @SerialName("timeAgo") val timeAgo: String,
    @SerialName("type") val type: String
)

// --- MAPPING EXTENSIONS ---

fun PixelfedMessageDto.toDomain(): Message {
    return Message(
        hidden = this.hidden,
        id = this.id,
        isAuthor = this.isAuthor,
        reportId = this.reportId,
        seen = this.seen,
        text = this.text,
        timeAgo = this.timeAgo,
        type = this.type
    )
}

fun Message.toDto(): PixelfedMessageDto {
    return PixelfedMessageDto(
        hidden = this.hidden,
        id = this.id,
        isAuthor = this.isAuthor,
        reportId = this.reportId,
        seen = this.seen,
        text = this.text,
        timeAgo = this.timeAgo,
        type = this.type
    )
}