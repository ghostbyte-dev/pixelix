package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedMediaAttachmentDto(
    @SerialName("id") val id: String,
    @SerialName("url") val url: String?,
    @SerialName("preview_url") val previewUrl: String?,
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
        previewUrl = this.previewUrl ?: "",
        metadata = null,
        blurHash = this.blurHash,
        type = this.type,
        description = this.description,
        license = this.license?.toDomain(),
        aspectRatio = this.meta?.original?.aspect,
        location = null
    )
}