package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNotificationDto(
    @SerialName("account") val account: PixelfedAccountDto = PixelfedAccountDto(),
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("status") val post: PixelfedPostDto?,
    @SerialName("created_at") val createdAt: String
)

fun PixelfedNotificationDto.toDomain(): Notification {
    return Notification(
        account = this.account.toDomain(),
        id = this.id,
        type = this.type,
        post = this.post?.toDomain(),
        createdAt = this.createdAt
    )
}