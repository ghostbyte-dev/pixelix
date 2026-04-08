package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Visibility {
    @SerialName("public") PUBLIC,
    @SerialName("unlisted") UNLISTED,
    @SerialName("private") PRIVATE,
    @SerialName("direct") DIRECT
}