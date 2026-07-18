package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.NewMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNewMessageDto(
    @SerialName("to_id") val toId: String,
    @SerialName("message") val message: String,
    @SerialName("type") val type: String
)


fun PixelfedNewMessageDto.toDomain(): NewMessage {
    return NewMessage(
        toId = this.toId,
        message = this.message,
        type = this.type
    )
}

fun NewMessage.toDto(): PixelfedNewMessageDto {
    return PixelfedNewMessageDto(
        toId = this.toId,
        message = this.message,
        type = this.type
    )
}