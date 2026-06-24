package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageRelationshipDto(
    @SerialName("userId") val userId: String,
    @SerialName("following") val following: Boolean,
    @SerialName("followedBy") val followedBy: Boolean,
    @SerialName("mutedNotifications") val mutedNotifications: Boolean,
    @SerialName("mutedReblogs") val mutedReblogs: Boolean,
    @SerialName("mutedStatuses") val mutedStatuses: Boolean,
    @SerialName("blocked") val blocked: Boolean,
    @SerialName("requested") val requested: Boolean,
    @SerialName("requestedBy") val requestedBy: Boolean
): DtoMappable<Relationship> {
    override fun toDomain(): Relationship {
        return Relationship(
            id = this.userId,
            following = this.following,
            followedBy = this.followedBy,
            muted = false,
            blocked = this.blocked,
            mutedNotifications = this.mutedNotifications,
            mutedReblogs = this.mutedReblogs,
            mutedStatuses = this.mutedStatuses,
            requested = this.requested,
            requestedBy = this.requestedBy
        )
    }
}
