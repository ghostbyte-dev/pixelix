package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Visibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


//TODO: check and find middle ground for pixelfed and vernissage, and show correct for each platform in ui
@Serializable
enum class VernissageVisibilityDto {
    @SerialName("public") PUBLIC,
    @SerialName("quietPublic") QUIETPUBLIC,
    @SerialName("followers") FOLLOWERS,
    @SerialName("mentioned") MENTIONED
}

fun VernissageVisibilityDto.toDomain(): Visibility = when (this) {
    VernissageVisibilityDto.PUBLIC   -> Visibility.PUBLIC
    VernissageVisibilityDto.QUIETPUBLIC -> Visibility.UNLISTED
    VernissageVisibilityDto.FOLLOWERS  -> Visibility.PRIVATE
    VernissageVisibilityDto.MENTIONED   -> Visibility.DIRECT
}
