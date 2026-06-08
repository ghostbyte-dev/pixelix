package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Visibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VernissageVisibilityDto {
    @SerialName("public") PUBLIC,
    @SerialName("unlisted") UNLISTED,
    @SerialName("private") PRIVATE,
    @SerialName("direct") DIRECT
}

fun VernissageVisibilityDto.toDomain(): Visibility = when (this) {
    VernissageVisibilityDto.PUBLIC   -> Visibility.PUBLIC
    VernissageVisibilityDto.UNLISTED -> Visibility.UNLISTED
    VernissageVisibilityDto.PRIVATE  -> Visibility.PRIVATE
    VernissageVisibilityDto.DIRECT   -> Visibility.DIRECT
}
