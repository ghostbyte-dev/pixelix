package com.daniebeler.pfpixelix.domain.service.capabilities

data class Capabilities(
    val post: PostCapabilities,
    val profile: ProfileCapabilities
)

data class PostCapabilities(
    val showCameraMetadata: Boolean
)

data class ProfileCapabilities(
    val showCollectionsOwnProfile: Boolean,
)