package com.daniebeler.pfpixelix.domain.service.capabilities

val VernissageCapabilities = Capabilities(
    post = PostCapabilities(
        showCameraMetadata = true,
        showLikedBy = false
    ),
    profile = ProfileCapabilities(
        showCollectionsOwnProfile = false,
        showAdvancedMuteOptions = true,
        blockReason = true
    ),
    notification = NotificationCapabilities(
        supportsFollowRequetActions = false
    )
)