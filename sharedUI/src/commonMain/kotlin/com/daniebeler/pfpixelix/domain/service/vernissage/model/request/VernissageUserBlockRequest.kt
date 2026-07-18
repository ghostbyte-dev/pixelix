package com.daniebeler.pfpixelix.domain.service.vernissage.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageUserBlockRequest(
    @SerialName("reason") val reason: String
)
