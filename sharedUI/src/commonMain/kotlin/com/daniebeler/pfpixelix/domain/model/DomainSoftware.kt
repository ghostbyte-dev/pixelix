package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DomainSoftware(
    @SerialName("name") val name: String,
    @SerialName("version") val version: String?
)