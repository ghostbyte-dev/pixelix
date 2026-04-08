package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse (
    @SerialName("msg") val message: String,
    @SerialName("code") val code: Int
)