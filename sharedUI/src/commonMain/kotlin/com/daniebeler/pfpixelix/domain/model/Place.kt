package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Place(
    @SerialName("id") val id: String,
    @SerialName("slug") val slug: String?,
    @SerialName("name") val name: String?,
    @SerialName("country") val country: String?,
    @SerialName("url") val url: String?
)
