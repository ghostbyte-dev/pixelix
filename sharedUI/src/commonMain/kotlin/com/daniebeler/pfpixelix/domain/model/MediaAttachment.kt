package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaAttachment(
    @SerialName("id") val id: String,
    @SerialName("url") val url: String?,
    @SerialName("preview_url") val previewUrl: String,
    @SerialName("meta") val meta: Meta?,
    @SerialName("blurhash") val blurHash: String?,
    @SerialName("type") val type: String,
    @SerialName("description") val description: String?,
    @SerialName("license") val license: License?
)
