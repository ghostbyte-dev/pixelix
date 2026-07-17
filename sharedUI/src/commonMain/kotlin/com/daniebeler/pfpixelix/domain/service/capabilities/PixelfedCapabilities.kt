package com.daniebeler.pfpixelix.domain.service.capabilities

val PixelfedCapabilities = Capabilities(
    general = GeneralCapabilities(supportsDMs = true),
    post = PostCapabilities(
        showCameraMetadata = false,
        showLikedBy = true
    ),
    profile = ProfileCapabilities(
        showCollectionsOwnProfile = true,
        showAdvancedMuteOptions = false,
        showRepostSettings = true,
        blockReason = false
    ),
    notification = NotificationCapabilities(
        supportsFollowRequestActions = true
    ),
    editProfile = EditProfileCapabilities(
        privateAccountToggle = true,
        manuallyAcceptNewFollowersToggle = false,
        includePostsInSearchEngineToggle = false,
        includeProfileInSearchEngineToggle = false,
        websiteField = true
    ),
    trending = TrendingCapabilities(
        supportsMultipleProfileTimeRanges = false,
        supportsMultipleHashtagTimeRanges = false
    ),
    newPost = NewPostCapabilities(
        supportsAdvancedMediaMetadata = false,
        includeDirectVisibility = false,
        showCountryDropdown = false,
        showLocationInputInGeneral = true,
        showLocationInputInImageTab = false,
        showMetadata = false,
        showCategoriesDropdown = false,
        supportLicenses = false,
    )
)