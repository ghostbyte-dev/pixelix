package com.daniebeler.pfpixelix.domain.model.request

import VernissageMediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedMediaAttachmentMetadataRequest

data class MediaAttachmentMetadataRequest(
    val id: String? = null,
//    @SerialName("url") val url: String = "",
//    val previewUrl: String = "",
    val description: String? = null,
//    @SerialName("blurhash") val blurhash: String? = null,
    val make: String? = null,
    val model: String? = null,
    val lens: String? = null,
    val createDate: String? = null,
    val focalLength: String? = null,
    val focalLenIn35mmFilm: String? = null,
    val fNumber: String? = null,
    val exposureTime: String? = null,
    val photographicSensitivity: String? = null,
    val software: String? = null,
    val film: String? = null,
    val chemistry: String? = null,
    val scanner: String? = null,
//    @SerialName("locationId") val locationId: String? = null,
//    @SerialName("licenseId") val licenseId: String? = null,
//    @SerialName("latitude") val latitude: String? = null,
//    @SerialName("longitude") val longitude: String? = null,
    val flash: String? = null
)

fun MediaAttachmentMetadataRequest.toPixelfed(): PixelfedMediaAttachmentMetadataRequest {
    return PixelfedMediaAttachmentMetadataRequest(
        description = this.description ?: ""
    )
}

fun MediaAttachmentMetadataRequest.toVernissage(): VernissageMediaAttachmentMetadataRequest {
    return VernissageMediaAttachmentMetadataRequest(
        id = this.id ?: "",
        description = this.description,
        make = this.make,
        model = this.model,
        lens = this.lens,
        createDate = this.createDate,
        focalLength = this.focalLength,
        focalLenIn35mmFilm = this.focalLenIn35mmFilm,
        fNumber = this.fNumber,
        exposureTime = this.exposureTime,
        photographicSensitivity = this.photographicSensitivity,
        software = this.software,
        film = this.film,
        chemistry = this.chemistry,
        scanner = this.scanner,
        flash = this.flash
    )
}