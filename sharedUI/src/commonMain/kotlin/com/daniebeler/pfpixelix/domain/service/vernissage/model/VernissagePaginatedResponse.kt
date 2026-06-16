package com.daniebeler.pfpixelix.domain.service.vernissage.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissagePaginatedResponse<T>(
    @SerialName("data") val data: T,
    @SerialName("maxId") val maxId: String
)