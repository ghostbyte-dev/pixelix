package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.NewPost
import com.daniebeler.pfpixelix.domain.model.Visibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNewPostDto(
    @SerialName("status") val status: String,
    @SerialName("media_ids") val mediaIds: List<String>,
    @SerialName("sensitive") val sensitive: Boolean,
    @SerialName("visibility") val visibility: PixelfedVisibilityDto,
    @SerialName("spoiler_text") val spoilerText: String?,
    @SerialName("place_id") val placeId: String?
)

// --- MAPPING EXTENSIONS ---

fun PixelfedNewPostDto.toDomain(): NewPost {
    return NewPost(
        status = this.status,
        mediaIds = this.mediaIds,
        sensitive = this.sensitive,
        visibility = this.visibility.toDomain(),
        spoilerText = this.spoilerText,
        placeId = this.placeId
    )
}
