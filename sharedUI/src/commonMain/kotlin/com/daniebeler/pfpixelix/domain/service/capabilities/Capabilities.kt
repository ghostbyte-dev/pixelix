package com.daniebeler.pfpixelix.domain.service.capabilities

data class Capabilities(
    val general: GeneralCapabilities,
    val post: PostCapabilities,
    val profile: ProfileCapabilities,
    val editProfile: EditProfileCapabilities,
    val notification: NotificationCapabilities,
    val trending: TrendingCapabilities
)

data class GeneralCapabilities(
    val supportsDMs: Boolean,
)

data class TrendingCapabilities(
    val supportsMultipleProfileTimeRanges: Boolean,
    val supportsMultipleHashtagTimeRanges: Boolean,
)

data class PostCapabilities(
    val showCameraMetadata: Boolean,
    val showLikedBy: Boolean
)

data class ProfileCapabilities(
    val showCollectionsOwnProfile: Boolean,
    val showAdvancedMuteOptions: Boolean,
    val blockReason: Boolean
)

data class EditProfileCapabilities(
    val privateAccountToggle: Boolean,
    val manuallyAcceptNewFollowersToggle: Boolean,
    val includePostsInSearchEngineToggle: Boolean,
    val includeProfileInSearchEngineToggle: Boolean,
    val websiteField: Boolean,
)

data class NotificationCapabilities(
    val supportsFollowRequestActions: Boolean
)