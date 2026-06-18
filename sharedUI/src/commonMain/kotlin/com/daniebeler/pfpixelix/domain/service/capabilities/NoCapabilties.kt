package com.daniebeler.pfpixelix.domain.service.capabilities

val NoCapabilities = Capabilities(
    post = PostCapabilities(
        showCameraMetadata = false
    ),
    profile = ProfileCapabilities(
        showCollectionsOwnProfile = false,
        showAdvancedMuteOptions = false,
        blockReason = false
    )
)