package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.LikedBy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedLikedByDto(
    @SerialName("id") val id: String?,
    @SerialName("username") val username: String?,
    @SerialName("others") val others: Boolean,
    @SerialName("total_count") val totalCount: Int = 0
)

fun PixelfedLikedByDto.toDomain(): LikedBy {
    return LikedBy(
        id = this.id,
        username = this.username,
        others = this.others,
        totalCount = this.totalCount
    )
}