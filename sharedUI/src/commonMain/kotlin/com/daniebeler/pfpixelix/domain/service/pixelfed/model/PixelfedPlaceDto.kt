package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedPlaceDto(
    @SerialName("id") val id: String,
    @SerialName("slug") val slug: String?,
    @SerialName("name") val name: String?,
    @SerialName("country") val country: String?,
    @SerialName("url") val url: String?
)

fun PixelfedPlaceDto.toDomain(): Place {
    return Place(
        id = this.id,
        slug = this.slug,
        name = this.name,
        country = this.country,
        url = this.url
    )
}