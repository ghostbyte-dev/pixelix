package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Configuration (
    @SerialName("media_attachments") val mediaAttachmentConfig: MediaAttachmentConfiguration,
    @SerialName("statuses") val statusConfig: StatusConfiguration
)