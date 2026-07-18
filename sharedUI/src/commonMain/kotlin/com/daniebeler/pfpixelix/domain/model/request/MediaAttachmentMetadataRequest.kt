package com.daniebeler.pfpixelix.domain.model.request

import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageMediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedMediaAttachmentMetadataRequest
import kotlin.time.Instant

data class MediaAttachmentMetadataRequest(
    val id: String? = null,
//    @SerialName("url") val url: String = "",
//    val previewUrl: String = "",
    val description: String? = null,
    val blurhash: String? = null,
    val make: FieldState<String> = FieldState(),
    val model: FieldState<String> = FieldState(),
    val lens: FieldState<String> = FieldState(),
    val createDate: FieldState<Instant> = FieldState(),
    val focalLength: FieldState<String> = FieldState(),
    val focalLenIn35mmFilm: FieldState<String> = FieldState(),
    val fNumber: FieldState<String> = FieldState(),
    val exposureTime: FieldState<String> = FieldState(),
    val photographicSensitivity: FieldState<String> = FieldState(),
    val software: FieldState<String> = FieldState(),
    val film: FieldState<String> = FieldState(),
    val chemistry: FieldState<String> = FieldState(),
    val scanner: FieldState<String> = FieldState(),
    val locationId: String? = null,
    val license: License? = null,
    val gpsData: FieldState<GPSData> = FieldState(),
    val flash: FieldState<String> = FieldState(),
)

data class GPSData(
    val lat: String,
    val long: String
)

data class FieldState<T>(
    val value: T?,
    val isIncluded: Boolean = true
) {
    val valueIfIncluded: T?
        get() = if (isIncluded) value else null

    constructor(value: T?) : this(
        value = value,
        isIncluded = when (value) {
            null -> false
            is String -> value.isNotBlank()
            else -> true
        }
    )
    constructor(): this(
        value = null,
        isIncluded = false
    )
}

fun MediaAttachmentMetadataRequest.toPixelfed(): PixelfedMediaAttachmentMetadataRequest {
    return PixelfedMediaAttachmentMetadataRequest(
        description = this.description ?: ""
    )
}

fun MediaAttachmentMetadataRequest.toVernissage(): VernissageMediaAttachmentMetadataRequest {
    return VernissageMediaAttachmentMetadataRequest(
        id = this.id ?: "",
        description = this.description,
        make = this.make.valueIfIncluded,
        model = this.model.valueIfIncluded,
        lens = this.lens.valueIfIncluded,
        createDate = this.createDate.valueIfIncluded.toString(),
        focalLength = this.focalLength.valueIfIncluded,
        focalLenIn35mmFilm = this.focalLenIn35mmFilm.valueIfIncluded,
        fNumber = this.fNumber.valueIfIncluded,
        exposureTime = this.exposureTime.valueIfIncluded,
        photographicSensitivity = this.photographicSensitivity.valueIfIncluded,
        software = this.software.valueIfIncluded,
        film = this.film.valueIfIncluded,
        chemistry = this.chemistry.valueIfIncluded,
        scanner = this.scanner.valueIfIncluded,
        flash = this.flash.valueIfIncluded,
        locationId = this.locationId,
        licenseId = this.license?.id,
        blurhash = this.blurhash,
        latitude = this.gpsData.value?.lat,
        longitude = this.gpsData.value?.long
    )
}