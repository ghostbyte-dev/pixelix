package com.daniebeler.pfpixelix.domain.service.capabilities

val VernissageCapabilities = Capabilities(
    post = PostCapabilities(
        showCameraMetadata = true
    ),
    profile = ProfileCapabilities(
        showCollectionsOwnProfile = false,
        showAdvancedMuteOptions = true,
        blockReason = true
    )
)