package com.daniebeler.pfpixelix.domain.service.capabilities

data class Capabilities(
    val post: PostCapabilities
)

data class PostCapabilities(
    val showCameraMetadata: Boolean
)
