package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Relationship
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedRelationshipDto(
    @SerialName("id") val id: String,
    @SerialName("following") val following: Boolean,
    @SerialName("followed_by") val followedBy: Boolean,
    @SerialName("muting") val muting: Boolean,
    @SerialName("blocking") val blocking: Boolean,
    @SerialName("requested") val requested: Boolean
)

fun PixelfedRelationshipDto.toDomain(): Relationship {
    return Relationship(
        id = this.id,
        following = this.following,
        followedBy = this.followedBy,
        muted = this.muting,
        blocked = this.blocking,
        requested = this.requested,
        requestedBy = false
    )
}