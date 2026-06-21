package com.daniebeler.pfpixelix.domain.service.capabilities

val PixelfedCapabilities = Capabilities(
    post = PostCapabilities(
        showCameraMetadata = false,
        showLikedBy = true
    ),
    profile = ProfileCapabilities(
        showCollectionsOwnProfile = true,
        showAdvancedMuteOptions = false,
        blockReason = false
    ),
    notification = NotificationCapabilities(
        supportsFollowRequetActions = true
    )
)