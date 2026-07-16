package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Country
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.MediaMetadata
import com.daniebeler.pfpixelix.domain.model.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VernissageUploadedAttachment(
    @SerialName("id") val id: String,
    @SerialName("url") val url: String?,
    @SerialName("preview_url") val previewUrl: String?,
    @SerialName("blurhash") val blurHash: String?,
    @SerialName("description") val description: String?,
)

@Serializable
data class VernissageMediaAttachmentDto(
    @SerialName("id") val id: String,
    @SerialName("smallFile") val smallFile: VernissageFileDto,
    @SerialName("originalFile") val originalFile: VernissageFileDto,
    @SerialName("metadata") val metadata: VernissageMetaDto?,
    @SerialName("blurhash") val blurHash: String?,
    @SerialName("description") val description: String?,
    @SerialName("license") val license: VernissageLicenseDto?,
    @SerialName("location") val location: VernissageLocationDto?
)

@Serializable
data class VernissageLicenseDto(
    @SerialName("name") val name: String?,
    @SerialName("code") val code: String?,
    @SerialName("id") val id: String?,
    @SerialName("url") val url: String?
)

@Serializable
data class VernissageLocationDto(
    @SerialName("country") val country: VernissageCountryDto?,
    @SerialName("id") val id: String,
    @SerialName("name") val name: String?,
    @SerialName("latitude") val lat: String?,
    @SerialName("longitude") val long: String?,
)

@Serializable
data class VernissageCountryDto(
    @SerialName("name") val name: String,
    @SerialName("code") val code: String,
    @SerialName("id") val id: String
)

@Serializable
data class VernissageFileDto(
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

fun VernissageUploadedAttachment.toDomain(): MediaAttachment {
    return MediaAttachment(
        id = this.id,
        url = this.url,
        previewUrl = this.previewUrl,
        blurHash = this.blurHash,
        description = this.description,
        metadata = null,
        type = null,
        location = null,
        license = null,
        aspectRatio = null
    )
}

fun VernissageMediaAttachmentDto.toDomain(): MediaAttachment {
    return MediaAttachment(
        id = this.id,
        url = this.originalFile.url,
        previewUrl = this.smallFile.url,
        metadata = this.metadata?.toDomain(),
        blurHash = this.blurHash,
        type = "",
        description = this.description,
        license = this.license?.toDomain(),
        aspectRatio = this.smallFile.aspect,
        location = this.location?.toDomain()
    )
}

fun VernissageLicenseDto.toDomain(): License {
    return License(
        code = this.code,
        id = this.id,
        name = this.name,
        url = this.url,
    )
}

fun VernissageLocationDto.toDomain(): Location {
    return Location(
        id = this.id,
        name = this.name,
        latitude = this.lat,
        longitude = this.long,
        country = this.country?.toDomain()
    )
}

fun VernissageCountryDto.toDomain(): Country {
    return Country(
        id = this.id,
        name = this.name,
        code = this.code
    )
}

fun VernissageMetaDto.toDomain(): MediaMetadata {
    return MediaMetadata(
        createDate = this.exif?.createDate,
        exposureTime = this.exif?.exposureTime,
        fNumber = this.exif?.fNumber,
        flash = this.exif?.flash,
        focalLenIn35mmFilm = this.exif?.focalLenIn35mmFilm,
        lens = this.exif?.lens,
        make = this.exif?.make,
        model = this.exif?.model,
        photographicSensitivity = this.exif?.photographicSensitivity,
        software = this.exif?.software,
        focalLength = this.exif?.focalLength
    )
}