package com.daniebeler.pfpixelix.domain.model

/**
 * Media data for a post
 *
 * @property url full resolution media
 * @property previewUrl smaller media file, correct aspect ratio
 * @property thumbnail smallest file, aspect ratio of 1:1
 *
 */
data class MediaAttachment(
    val id: String,
    val url: String,
    val previewUrl: String?,
    val thumbnail: String?,
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
    val software: String?,
    val film: String?,
    val chemistry: String?,
    val scanner: String?,
    val latitude: String? = null,
    val longitude: String? = null,
)