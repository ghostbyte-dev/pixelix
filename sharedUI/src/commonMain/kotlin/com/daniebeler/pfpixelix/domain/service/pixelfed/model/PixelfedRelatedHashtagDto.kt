package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedRelatedHashtagDto(
    @SerialName("name") val name: String,
    @SerialName("related_count") val relatedCount: Int
)

fun PixelfedRelatedHashtagDto.toDomain(): RelatedHashtag {
    return RelatedHashtag(
        name = this.name,
        relatedCount = this.relatedCount
    )
}