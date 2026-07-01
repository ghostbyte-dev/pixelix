package com.daniebeler.pfpixelix.domain.model.request

import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUserMuteRequest

data class UserMuteRequest(
    val mute: Boolean? = null,
    val muteStatuses: Boolean? = null,
    val muteReblogs: Boolean? = null,
    val muteNotifications: Boolean? = null,
    val removeStatusesFromTimeline: Boolean? = null,
    val removeReblogsFromTimeline: Boolean? = null,
    val endDate: kotlin.time.Instant? = null
)

fun UserMuteRequest.toVernissage(): VernissageUserMuteRequest {
    return VernissageUserMuteRequest(
        muteStatuses = this.muteStatuses ?: false,
        muteReblogs = this.muteReblogs ?: false,
        muteNotifications = this.muteNotifications ?: false,
        removeStatusesFromTimeline = this.removeStatusesFromTimeline ?: false,
        removeReblogsFromTimeline = this.removeReblogsFromTimeline ?: false,
        endDate = this.endDate
    )
}