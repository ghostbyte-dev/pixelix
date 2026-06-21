package com.daniebeler.pfpixelix.domain.service.capabilities

val NoCapabilities = Capabilities(
    post = PostCapabilities(
        showCameraMetadata = false,
        showLikedBy = false
    ),
    profile = ProfileCapabilities(
        showCollectionsOwnProfile = false,
        showAdvancedMuteOptions = false,
        blockReason = false
    ),
    notification = NotificationCapabilities(
        supportsFollowRequetActions = false
    )
)