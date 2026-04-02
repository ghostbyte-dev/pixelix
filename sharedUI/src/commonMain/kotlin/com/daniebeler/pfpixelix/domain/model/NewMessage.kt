package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewMessage(
    @SerialName("to_id") val toId: String,
    @SerialName("message") val message: String,
    @SerialName("type") val type: String
)

