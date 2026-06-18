package com.daniebeler.pfpixelix.domain.model.request

import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUserMuteRequest

data class UserMuteRequest(
    val muteStatuses: Boolean = false,
    val muteReblogs: Boolean = false,
    val muteNotifications: Boolean = false,
    val removeStatusesFromTimeline: Boolean = false,
    val removeReblogsFromTimeline: Boolean = false,
)
//TODO: add endDate

fun UserMuteRequest.toVernissage(): VernissageUserMuteRequest {
    return VernissageUserMuteRequest(
        muteStatuses = this.muteStatuses,
        muteReblogs = this.muteReblogs,
        muteNotifications = this.muteNotifications,
        removeStatusesFromTimeline = this.removeStatusesFromTimeline,
        removeReblogsFromTimeline = this.removeReblogsFromTimeline
    )
}