package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.repository.serializers.TagNameSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedTagDto(
    @Serializable(with = TagNameSerializer::class)
    @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("following") val following: Boolean = false,
    @SerialName("count") val count: Int = 0,
    @SerialName("total") val total: Int = 0,
    @SerialName("hashtag") val hashtag: String? = null
)

fun PixelfedTagDto.toDomain(): Tag {
    return Tag(
        name = this.name,
        url = this.url,
        following = this.following,
        count = this.count,
        total = this.total,
        hashtag = this.hashtag
    )
}