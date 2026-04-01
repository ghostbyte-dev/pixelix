package com.daniebeler.pfpixelix.domain.model

import com.daniebeler.pfpixelix.domain.repository.serializers.TagNameSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @Serializable(with = TagNameSerializer::class) @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("following") val following: Boolean = false,
    @SerialName("count") val count: Int = 0,
    @SerialName("total") val total: Int = 0,
    @SerialName("hashtag") val hashtag: String?
)