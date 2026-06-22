package com.daniebeler.pfpixelix.domain.service.capabilities

val VernissageCapabilities = Capabilities(
    general = GeneralCapabilities(supportsDMs = false),
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
        supportsFollowRequestActions = false
    ),
    editProfile = EditProfileCapabilities(
        privateAccountToggle = false,
        manuallyAcceptNewFollowersToggle = true,
        includePostsInSearchEngineToggle = true,
        includeProfileInSearchEngineToggle = true,
        websiteField = false
    )
)