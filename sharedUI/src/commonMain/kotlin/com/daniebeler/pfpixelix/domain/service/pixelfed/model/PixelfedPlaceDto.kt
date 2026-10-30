package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Country
import com.daniebeler.pfpixelix.domain.model.Place
import com.daniebeler.pfpixelix.utils.toCoordinateOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedPlaceDto(
    @SerialName("id") val id: String,
    @SerialName("slug") val slug: String?,
    @SerialName("name") val name: String?,
    @SerialName("country") val country: String?,
    @SerialName("url") val url: String?,
    @SerialName("lat") val lat: String?,
    @SerialName("long") val long: String?
)

fun PixelfedPlaceDto.toDomain(): Place {
    return Place(
        id = this.id,
        name = this.name,
        latitude = this.lat.toCoordinateOrNull()?.takeIf { it in -90.0..90.0 },
        longitude = this.long.toCoordinateOrNull()?.takeIf { it in -180.0..180.0 },
        country = this.country?.let { Country(id = null, name = it, code = null) },
        slug = this.slug
    )
}