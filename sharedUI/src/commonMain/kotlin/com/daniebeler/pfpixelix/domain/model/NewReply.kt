package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewReply(
    @SerialName("status") val status: String,
    @SerialName("in_reply_to_id") val toId: String
)

