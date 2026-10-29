package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.UpdatePost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedUpdatePostDto(
    @SerialName("status") val status: String,
    @SerialName("media_ids") val mediaIds: List<String>?,
    @SerialName("sensitive") val sensitive: Boolean?,
    @SerialName("spoiler_text") val spoilerText: String?,
    @SerialName("location") val location: PixelfedPlaceDto?
)

// --- MAPPING EXTENSIONS ---

// Convert API responses to Domain models if needed
fun PixelfedUpdatePostDto.toDomain(): UpdatePost {
    return UpdatePost(
        status = this.status,
        mediaIds = this.mediaIds,
        sensitive = this.sensitive,
        spoilerText = this.spoilerText,
        place = this.location?.toDomain()
    )
}
