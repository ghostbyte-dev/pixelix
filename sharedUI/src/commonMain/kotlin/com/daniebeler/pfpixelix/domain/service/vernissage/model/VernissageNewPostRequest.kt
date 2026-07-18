package com.daniebeler.pfpixelix.domain.service.vernissage.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageNewPostRequest(
    @SerialName("note") val note: String,
    @SerialName("attachmentIds") val mediaIds: List<String>,
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("contentWarning") val contentWarning: String?,
    @SerialName("visibility") val visibility: VernissageVisibilityDto,
    @SerialName("commentsDisabled") val commentsDisabled: Boolean = false,
    @SerialName("categoryId") val categoryId: String?
)
