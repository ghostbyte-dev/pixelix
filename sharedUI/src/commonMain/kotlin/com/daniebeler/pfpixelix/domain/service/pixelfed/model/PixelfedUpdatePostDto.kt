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
        location = this.location?.toDomain()
    )
}

// Convert Domain payload to DTO format for request bodies
fun UpdatePost.toDto(): PixelfedUpdatePostDto {
    return PixelfedUpdatePostDto(
        status = this.status,
        mediaIds = this.mediaIds,
        sensitive = this.sensitive,
        spoilerText = this.spoilerText,
        // Assuming PixelfedPlaceDto has an expansion mapping or you add a toDto() on Place
        location = this.location?.let {
            PixelfedPlaceDto(
                id = it.id,
                slug = it.slug,
                name = it.name,
                country = it.country,
                url = it.url
            )
        }
    )
}