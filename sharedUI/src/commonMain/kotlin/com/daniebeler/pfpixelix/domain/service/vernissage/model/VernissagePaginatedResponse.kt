package com.daniebeler.pfpixelix.domain.service.vernissage.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissagePaginatedResponse<T>(
    @SerialName("data") val data: List<T>,
    @SerialName("maxId") val maxId: String?
)

@Serializable
data class VernissagePagePaginatedResponse<T>(
    @SerialName("data") val data: List<T>,
    @SerialName("page") val page: Int,
    @SerialName("size") val size: Int,
    @SerialName("total") val total: Int,
)