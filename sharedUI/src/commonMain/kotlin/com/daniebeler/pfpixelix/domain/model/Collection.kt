package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    @SerialName("id") val id: String,
    @SerialName("visibility") val visibility: String,
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("thumb") val thumbnail: String,
    @SerialName("post_count") val postCount: Int,
    @SerialName("username") val username: String,
    @SerialName("url") val url: String
)
