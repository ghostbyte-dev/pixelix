package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Emoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedEmojiDto(
    @SerialName("shortcode") val shortcode: String,
    @SerialName("url") val url: String,
    @SerialName("static_url") val staticUrl: String
)

fun PixelfedEmojiDto.toDomain(): Emoji {
    return Emoji(
        shortcode = this.shortcode,
        url = this.url,
        staticUrl = this.staticUrl
    )
}