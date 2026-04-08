package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelatedHashtag(
    @SerialName("name") val name: String,
    @SerialName("related_count") val relatedCount: Int
)