package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Collection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedCollectionDto(
    @SerialName("id") val id: String,
    @SerialName("visibility") val visibility: String,
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("thumb") val thumbnail: String,
    @SerialName("post_count") val postCount: Int,
    @SerialName("username") val username: String,
    @SerialName("url") val url: String
)


fun PixelfedCollectionDto.toDomain(): Collection {
    return Collection(
        id = this.id,
        visibility = this.visibility,
        title = this.title,
        description = this.description,
        thumbnail = this.thumbnail,
        postCount = this.postCount,
        username = this.username,
        url = this.url
    )
}
