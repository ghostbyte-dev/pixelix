package com.daniebeler.pfpixelix.domain.service.capabilities

val PixelfedCapabilities = Capabilities(
    post = PostCapabilities(
        showCameraMetadata = false
    ),
    profile = ProfileCapabilities(
        showCollectionsOwnProfile = true,
        showAdvancedMuteOptions = false,
        blockReason = false
    )
)