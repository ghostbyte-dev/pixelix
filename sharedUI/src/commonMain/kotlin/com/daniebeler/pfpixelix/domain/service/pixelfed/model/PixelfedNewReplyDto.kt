package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.NewReply
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNewReplyDto(
    @SerialName("status") val status: String,
    @SerialName("in_reply_to_id") val toId: String
)

fun PixelfedNewReplyDto.toDomain(): NewReply {
    return NewReply(
        status = this.status,
        toId = this.toId
    )
}

fun NewReply.toDto(): PixelfedNewReplyDto {
    return PixelfedNewReplyDto(
        status = this.status,
        toId = this.toId
    )
}