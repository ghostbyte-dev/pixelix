package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    @SerialName("shortcode") val shortcode: String,
    @SerialName("url") val url: String,
    @SerialName("static_url") val staticUrl: String
)
