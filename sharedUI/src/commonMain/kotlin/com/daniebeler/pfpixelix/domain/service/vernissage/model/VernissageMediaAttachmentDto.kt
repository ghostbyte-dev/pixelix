package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.Meta
import com.daniebeler.pfpixelix.domain.model.Original
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedLicenseDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO: add location
@Serializable
data class VernissageMediaAttachmentDto(
    @SerialName("id") val id: String,
    @SerialName("smallFile") val smallFile: VernissageFileDto,
    @SerialName("originalFile") val originalFile: VernissageFileDto,
    @SerialName("metadata") val metadata: VernissageMetaDto?,
    @SerialName("blurhash") val blurHash: String?,
    @SerialName("description") val description: String?,
)

@Serializable data class VernissageFileDto(
    @SerialName("aspect") val aspect: Double,
    @SerialName("url") val url: String
)

@Serializable
data class VernissageMetaDto(
    @SerialName("exif") val exif: VernissageExifDto? = null
)

@Serializable
data class VernissageExifDto(
    @SerialName("createDate") val createDate: String? = null,
    @SerialName("exposureTime") val exposureTime: String? = null,
    @SerialName("fNumber") val fNumber: String? = null,
    @SerialName("flash") val flash: String? = null,
    @SerialName("focalLenIn35mmFilm") val focalLenIn35mmFilm: String? = null,
    @SerialName("focalLength") val focalLength: String? = null,
    @SerialName("lens") val lens: String? = null,
    @SerialName("make") val make: String? = null,
    @SerialName("model") val model: String? = null,
    @SerialName("photographicSensitivity") val photographicSensitivity: String? = null,
    @SerialName("software") val software: String? = null
)

// --- MAPPING EXTENSIONS ---

fun VernissageMediaAttachmentDto.toDomain(): MediaAttachment {
    return MediaAttachment(
        id = this.id,
        url = this.smallFile.url,
        previewUrl = this.smallFile.url,
        meta = Meta(
            original = Original(
                aspect = this.smallFile.aspect
            )
        ),
        blurHash = this.blurHash,
        type = "",
        description = this.description,
        license = null
    )
}