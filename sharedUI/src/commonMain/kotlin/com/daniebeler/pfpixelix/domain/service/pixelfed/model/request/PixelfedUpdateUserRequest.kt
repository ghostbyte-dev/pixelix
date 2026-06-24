package com.daniebeler.pfpixelix.domain.service.pixelfed.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedUpdateUserRequest(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("locked") val locked: Boolean
)