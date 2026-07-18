package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Visibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PixelfedVisibilityDto {
    @SerialName("public") PUBLIC,
    @SerialName("unlisted") UNLISTED,
    @SerialName("private") PRIVATE,
    @SerialName("direct") DIRECT
}

fun PixelfedVisibilityDto.toDomain(): Visibility = when (this) {
    PixelfedVisibilityDto.PUBLIC   -> Visibility.PUBLIC
    PixelfedVisibilityDto.UNLISTED -> Visibility.UNLISTED
    PixelfedVisibilityDto.PRIVATE  -> Visibility.PRIVATE
    PixelfedVisibilityDto.DIRECT   -> Visibility.DIRECT
}
