package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Identifiable
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.NotificationType
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedAccountDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedPostDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageNotificationDto(
    @SerialName("byUser") val byUser: VernissageAccountDto,
    @SerialName("id") val id: String,
    @SerialName("notificationType") val type: VernissageNotificationType,
    @SerialName("status") val post: VernissagePostDto?,
    @SerialName("createdAt") val createdAt: String
): DtoMappable<Notification> {
    override fun toDomain(): Notification {
        return Notification(
            account = this.byUser.toDomain(),
            id = this.id,
            type = this.type.toDomain(),
            post = this.post?.toDomain(),
            createdAt = this.createdAt
        )
    }
}

@Serializable
enum class VernissageNotificationType {
    @SerialName("mention") MENTION,
    @SerialName("status") STATUS,
    @SerialName("reblog") REBLOG,
    @SerialName("follow") FOLLOW,
    @SerialName("followRequest") FOLLOW_REQUEST,
    @SerialName("favourite") FAVOURITE,
    @SerialName("update") UPDATE,
    @SerialName("newComment") NEW_COMMENT,
    UNDEFINED;

    fun toDomain(): NotificationType {
        return when (this) {
            MENTION -> NotificationType.MENTION
            STATUS -> NotificationType.STATUS
            REBLOG -> NotificationType.REBLOG
            FOLLOW -> NotificationType.FOLLOW
            FOLLOW_REQUEST -> NotificationType.FOLLOW_REQUEST
            FAVOURITE -> NotificationType.FAVOURITE
            UPDATE -> NotificationType.UPDATE
            NEW_COMMENT -> NotificationType.NEW_COMMENT
            UNDEFINED -> NotificationType.UNDEFINED
        }
    }
}