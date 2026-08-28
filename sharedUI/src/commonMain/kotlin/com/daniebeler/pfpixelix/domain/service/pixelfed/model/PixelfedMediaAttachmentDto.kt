package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a media attachment returned by the Pixelfed API.
 *
 * @property id The unique identifier of the media attachment.
 * @property url The full-resolution direct URL of the media asset.
 * @property previewUrl The direct URL for the static or downscaled preview thumbnail of the media, it has aspect ratio of 1:1.
 * @property optimizedUrl The optimized version of the media asset URL, has the correct aspect ratio.
 * @property meta Technical metadata regarding dimensions, aspect ratio, or audio details.
 * @property blurHash The compact BlurHash representation for placeholder rendering.
 * @property type The type of attachment (e.g., "image", "video", "audio", "unknown").
 * @property description Alt text or user description for the media attachment.
 * @property license License information associated with the media asset.
 */
@Serializable
data class PixelfedMediaAttachmentDto(
    @SerialName("id") val id: String,
    @SerialName("url") val url: String?,
    @SerialName("preview_url") val previewUrl: String?,
    @SerialName("optimized_url") val optimizedUrl: String?,
    @SerialName("meta") val meta: PixelfedMetaDto?,
    @SerialName("blurhash") val blurHash: String?,
    @SerialName("type") val type: String,
    @SerialName("description") val description: String?,
    @SerialName("license") val license: PixelfedLicenseDto?
)

@Serializable
data class PixelfedMetaDto(
    @SerialName("original") val original: PixelfedOriginalDto?
)

@Serializable
data class PixelfedOriginalDto(
    @SerialName("aspect") val aspect: Double
)

fun PixelfedMediaAttachmentDto.toDomain(): MediaAttachment {
    return MediaAttachment(
        id = this.id,
        url = this.url ?: "",
        previewUrl = this.optimizedUrl,
        thumbnail = this.previewUrl,
        metadata = null,
        blurHash = this.blurHash,
        type = this.type,
        description = this.description,
        license = this.license?.toDomain(),
        aspectRatio = this.meta?.original?.aspect,
        location = null
    )
}