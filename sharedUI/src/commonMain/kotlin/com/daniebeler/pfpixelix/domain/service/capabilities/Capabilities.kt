package com.daniebeler.pfpixelix.domain.service.capabilities

data class Capabilities(
    val post: PostCapabilities,
    val profile: ProfileCapabilities,
    val notification: NotificationCapabilities
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
    val supportsFollowRequetActions: Boolean
)