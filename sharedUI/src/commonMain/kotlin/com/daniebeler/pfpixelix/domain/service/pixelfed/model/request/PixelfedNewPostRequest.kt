package com.daniebeler.pfpixelix.domain.service.pixelfed.model.request

import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedVisibilityDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNewPostRequest(
    @SerialName("status") val status: String,
    @SerialName("media_ids[]") val mediaIds: List<String>,
    @SerialName("sensitive") val sensitive: Boolean?,
    @SerialName("visibility") val visibility: PixelfedVisibilityDto?,
    @SerialName("spoiler_text") val spoilerText: String?,
    @SerialName("place_id") val placeId: String?,
    @SerialName("comments_disabled") val commentsDisabled: Boolean?
)