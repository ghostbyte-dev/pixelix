package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaAttachmentConfiguration(
    @SerialName("supported_mime_types") val supportedMimeTypes: List<String>,
    @SerialName("image_size_limit") val imageSizeLimit: Int,
    @SerialName("video_size_limit") val videoSizeLimit: Int
)
