package com.daniebeler.pfpixelix.domain.service.capabilities

//TODO: capabilities are not reloaded after account switch
data class Capabilities(
    val general: GeneralCapabilities,
    val post: PostCapabilities,
    val profile: ProfileCapabilities,
    val editProfile: EditProfileCapabilities,
    val notification: NotificationCapabilities,
    val trending: TrendingCapabilities,
    val newPost: NewPostCapabilities
)

data class GeneralCapabilities(
    val supportsDMs: Boolean,
    val supportsPosting: Boolean
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
    val blockReason: Boolean,
    val showRepostSettings: Boolean
)

data class EditProfileCapabilities(
    val privateAccountToggle: Boolean,
    val manuallyAcceptNewFollowersToggle: Boolean,
    val includePostsInSearchEngineToggle: Boolean,
    val includeProfileInSearchEngineToggle: Boolean,
    val websiteField: Boolean
)

data class NotificationCapabilities(
    val supportsFollowRequestActions: Boolean
)

data class NewPostCapabilities(
    val supportsAdvancedMediaMetadata: Boolean,
    val includeDirectVisibility: Boolean,
    val showCountryDropdown: Boolean,
    val showLocationInputInGeneral: Boolean,
    val showLocationInputInImageTab: Boolean,
    val showMetadata: Boolean,
    val showCategoriesDropdown: Boolean,
    val supportLicenses: Boolean,
)