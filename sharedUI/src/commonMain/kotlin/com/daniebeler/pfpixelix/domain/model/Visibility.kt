package com.daniebeler.pfpixelix.domain.model

import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedVisibilityDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageVisibilityDto

enum class Visibility {
    PUBLIC,
    UNLISTED,
    PRIVATE,
    DIRECT
}


fun Visibility.toVernissage(): VernissageVisibilityDto = when (this) {
    Visibility.PUBLIC   -> VernissageVisibilityDto.PUBLIC
    Visibility.UNLISTED -> VernissageVisibilityDto.QUIETPUBLIC
    Visibility.PRIVATE  -> VernissageVisibilityDto.FOLLOWERS
    Visibility.DIRECT   -> VernissageVisibilityDto.MENTIONED
}

fun Visibility.toPixelfed(): PixelfedVisibilityDto = when (this) {
    Visibility.PUBLIC -> PixelfedVisibilityDto.PUBLIC
    Visibility.UNLISTED -> PixelfedVisibilityDto.UNLISTED
    Visibility.PRIVATE -> PixelfedVisibilityDto.PRIVATE
    Visibility.DIRECT -> PixelfedVisibilityDto.DIRECT
}