package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Identifiable
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.NotificationType
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNotificationDto(
    @SerialName("account") val account: PixelfedAccountDto = PixelfedAccountDto(),
    @SerialName("id") val id: String,
    @SerialName("type") val type: PixelfedNotificationType,
    @SerialName("status") val post: PixelfedPostDto?,
    @SerialName("created_at") val createdAt: String
): DtoMappable<Notification> {
    override fun toDomain(): Notification {
        return Notification(
            account = this.account.toDomain(),
            id = this.id,
            type = this.type.toDomain(),
            post = this.post?.toDomain(),
            createdAt = this.createdAt
        )
    }
}

@Serializable
enum class PixelfedNotificationType {
    @SerialName("mention") MENTION,
    @SerialName("direct") DIRECT_MESSAGE,
    @SerialName("reblog") REBLOG,
    @SerialName("follow") FOLLOW,
    @SerialName("favourite") FAVOURITE,
    UNDEFINED;

    fun toDomain(): NotificationType {
        return when (this) {
            MENTION -> NotificationType.MENTION
            DIRECT_MESSAGE -> NotificationType.DIRECT_MESSAGE
            REBLOG -> NotificationType.REBLOG
            FOLLOW -> NotificationType.FOLLOW
            FAVOURITE -> NotificationType.FAVOURITE
            UNDEFINED -> NotificationType.UNDEFINED
        }
    }
}