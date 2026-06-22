package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Country
import com.daniebeler.pfpixelix.domain.model.Location
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

fun PixelfedPlaceDto.toDomain(): Location {
    return Location(
        id = this.id,
        name = this.name,
        latitude = null,
        longitude = null,
        country = this.country?.let { Country(id = null, name = it, code = null) })
}