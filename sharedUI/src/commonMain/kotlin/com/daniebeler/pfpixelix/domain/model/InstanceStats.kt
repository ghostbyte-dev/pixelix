package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceStats(
    @SerialName("user_count") val userCount: Int,
    @SerialName("status_count") val statusCount: Int,
    @SerialName("domain_count") val domainCount: Int
)
