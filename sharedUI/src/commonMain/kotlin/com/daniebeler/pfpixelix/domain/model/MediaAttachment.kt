package com.daniebeler.pfpixelix.domain.model

data class MediaAttachment(
    val id: String,
    val url: String?,
    val previewUrl: String?,
    val aspectRatio: Double?,
    val metadata: MediaMetadata?,
    val blurHash: String?,
    val type: String?,
    val description: String?,
    val license: License?,
    val location: Location?
)

data class MediaMetadata(
    val createDate: String?,
    val exposureTime: String?,
    val fNumber: String?,
    val flash: String?,
    val focalLenIn35mmFilm: String?,
    val focalLength: String?,
    val lens: String?,
    val make: String?,
    val model: String?,
    val photographicSensitivity: String?,
    val software: String?
)