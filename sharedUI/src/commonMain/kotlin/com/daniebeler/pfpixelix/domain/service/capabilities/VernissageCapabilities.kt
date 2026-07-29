package com.daniebeler.pfpixelix.domain.service.capabilities

val VernissageCapabilities = Capabilities(
    general = GeneralCapabilities(supportsDMs = false), post = PostCapabilities(
        showCameraMetadata = true, showLikedBy = false
    ), profile = ProfileCapabilities(
        showCollectionsOwnProfile = false,
        showAdvancedMuteOptions = true,
        showRepostSettings = false,
        blockReason = true
    ), notification = NotificationCapabilities(
        supportsFollowRequestActions = false
    ), editProfile = EditProfileCapabilities(
        privateAccountToggle = false,
        manuallyAcceptNewFollowersToggle = true,
        includePostsInSearchEngineToggle = true,
        includeProfileInSearchEngineToggle = true,
        websiteField = false,
        headerImage = true,
        includeFields = true
    ), trending = TrendingCapabilities(
        supportsMultipleProfileTimeRanges = true,
        supportsMultipleHashtagTimeRanges = true,
        supportsAdvancedCategories = true
    ), newPost = NewPostCapabilities(
        supportsAdvancedMediaMetadata = true,
        includeDirectVisibility = true,
        showCountryDropdown = true,
        showLocationInputInGeneral = false,
        showLocationInputInImageTab = true,
        showMetadata = true,
        showCategoriesDropdown = true,
        supportLicenses = true,
    )
)