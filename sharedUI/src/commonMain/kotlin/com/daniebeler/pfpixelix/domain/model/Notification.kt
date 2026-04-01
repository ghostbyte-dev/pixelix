package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("account") val account: Account = Account(),
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("status") val post: Post?,
    @SerialName("created_at") var createdAt: String
)