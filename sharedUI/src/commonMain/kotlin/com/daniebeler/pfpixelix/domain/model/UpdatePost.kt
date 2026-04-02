package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePost(
    val status: String,
    @SerialName("media_ids") val mediaIds: List<String>?,
    val sensitive: Boolean?,
    val spoilerText: String?,
    val location: Place?
)

