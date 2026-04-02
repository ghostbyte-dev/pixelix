package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    @SerialName("id") val id: String,
    @SerialName("following") val following: Boolean,
    @SerialName("followed_by") val followedBy: Boolean,
    @SerialName("muting") val muting: Boolean,
    @SerialName("blocking") val blocking: Boolean
)