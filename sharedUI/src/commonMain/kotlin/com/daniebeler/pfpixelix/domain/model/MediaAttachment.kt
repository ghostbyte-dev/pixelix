package com.daniebeler.pfpixelix.domain.model

data class MediaAttachment(
    val id: String,
    val url: String?,
    val previewUrl: String,
    val meta: Meta?,
    val blurHash: String?,
    val type: String,
    val description: String?,
    val license: License?
)

data class Meta(
    val original: Original?
)

data class Original(
    val aspect: Double
)