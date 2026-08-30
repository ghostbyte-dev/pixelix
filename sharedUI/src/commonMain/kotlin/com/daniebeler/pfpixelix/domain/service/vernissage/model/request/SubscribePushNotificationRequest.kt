package com.daniebeler.pfpixelix.domain.service.vernissage.model.request

import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageVisibilityDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribePushNotificationRequest(
    @SerialName("endpoint") val endpoint: String,
    @SerialName("userAgentPublicKey") val userAgentPublicKey: String,
    @SerialName("auth") val auth: String,
    @SerialName("webPushNotificationsEnabled") val webPushNotificationsEnabled: Boolean = true,
    @SerialName("webPushMentionEnabled") val webPushMentionEnabled: Boolean = true,
    @SerialName("webPushStatusEnabled") val webPushStatusEnabled: Boolean = true,
    @SerialName("webPushReblogEnabled") val webPushReblogEnabled: Boolean = true,
    @SerialName("webPushFollowEnabled") val webPushFollowEnabled: Boolean = true,
    @SerialName("webPushFollowRequestEnabled") val webPushFollowRequestEnabled: Boolean = true,
    @SerialName("webPushFavouriteEnabled") val webPushFavouriteEnabled: Boolean = true,
    @SerialName("webPushUpdateEnabled") val webPushUpdateEnabled: Boolean = true,
    @SerialName("webPushAdminSignUpEnabled") val webPushAdminSignUpEnabled: Boolean = true,
    @SerialName("webPushAdminReportEnabled") val webPushAdminReportEnabled: Boolean = true,
    @SerialName("webPushNewCommentEnabled") val webPushNewCommentEnabled: Boolean = true
)
