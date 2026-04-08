package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Instance(
    @SerialName("uri") val domain: String,
    @SerialName("rules") val rules: List<Rule>,
    @SerialName("short_description") val shortDescription: String,
    @SerialName("description") val description: String,
    @SerialName("thumbnail") val thumbnailUrl: String,
    @SerialName("contact_account") val admin: Account? = null,
    @SerialName("stats") val stats: InstanceStats,
    @SerialName("version") val version: String,
    @SerialName("configuration") val configuration: Configuration
)
