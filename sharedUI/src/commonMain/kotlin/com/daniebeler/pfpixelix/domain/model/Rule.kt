package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String
)
