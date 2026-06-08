package com.daniebeler.pfpixelix.domain.service.vernissage.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageResponse<T>(
    @SerialName("data") val data: T
)