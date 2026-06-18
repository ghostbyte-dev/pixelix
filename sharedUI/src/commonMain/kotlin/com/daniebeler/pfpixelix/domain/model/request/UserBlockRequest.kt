package com.daniebeler.pfpixelix.domain.model.request

import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUserBlockRequest

data class UserBlockRequest(
    val reason: String
)

fun UserBlockRequest.toVernissage(): VernissageUserBlockRequest {
    return VernissageUserBlockRequest(
        reason = this.reason
    )
}