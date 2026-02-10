package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusConfiguration(
    @SerialName("max_media_attachments") val maxMediaAttachments: Int,
    @SerialName("max_characters") val maxCharacters: Int?

)
