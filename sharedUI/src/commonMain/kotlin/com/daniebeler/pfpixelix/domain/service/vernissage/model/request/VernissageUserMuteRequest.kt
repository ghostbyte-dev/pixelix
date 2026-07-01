package com.daniebeler.pfpixelix.domain.service.vernissage.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class VernissageUserMuteRequest(
    @SerialName("muteStatuses") val muteStatuses: Boolean,
    @SerialName("muteReblogs") val muteReblogs: Boolean,
    @SerialName("muteNotifications") val muteNotifications: Boolean,
    @SerialName("removeStatusesFromTimeline") val removeStatusesFromTimeline: Boolean,
    @SerialName("removeReblogsFromTimeline") val removeReblogsFromTimeline: Boolean,
    @SerialName("endDate") val endDate: Instant? = null
)