package com.daniebeler.pfpixelix.domain.service.capabilities

val NoCapabilities = Capabilities(
    general = GeneralCapabilities(supportsDMs = false), post = PostCapabilities(
        showCameraMetadata = false, showLikedBy = false
    ), profile = ProfileCapabilities(
        showCollectionsOwnProfile = false, showAdvancedMuteOptions = false, blockReason = false
    ), notification = NotificationCapabilities(
        supportsFollowRequestActions = false
    ),
    editProfile = EditProfileCapabilities(
        privateAccountToggle = false,
        manuallyAcceptNewFollowersToggle = false,
        includePostsInSearchEngineToggle = false,
        includeProfileInSearchEngineToggle = false,
        websiteField = false
    )
)