package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikedBy(
    @SerialName("id") val id: String?,
    @SerialName("username") val username: String?,
    @SerialName("others") val others: Boolean,
    @SerialName("total_count") val totalCount: Int = 0
)
