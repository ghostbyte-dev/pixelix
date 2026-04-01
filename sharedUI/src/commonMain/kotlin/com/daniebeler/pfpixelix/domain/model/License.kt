package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class License(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String
)
