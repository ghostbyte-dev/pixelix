package com.daniebeler.pfpixelix.domain.service.capabilities

data class Capabilities(
    val general: GeneralCapabilities,
    val post: PostCapabilities,
    val profile: ProfileCapabilities,
    val notification: NotificationCapabilities
)

data class GeneralCapabilities(
    val supportsDMs: Boolean,
)

data class PostCapabilities(
    val showCameraMetadata: Boolean,
    val showLikedBy: Boolean
)

data class ProfileCapabilities(
    val showCollectionsOwnProfile: Boolean,
    val showAdvancedMuteOptions: Boolean,
    val blockReason: Boolean
)

data class NotificationCapabilities(
    val supportsFollowRequestActions: Boolean
)