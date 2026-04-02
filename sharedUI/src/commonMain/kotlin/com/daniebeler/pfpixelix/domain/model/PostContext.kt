package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostContext(
    @SerialName("ancestors") val ancestors: List<Post>,
    @SerialName("descendants") val descendants: List<Post>,
)