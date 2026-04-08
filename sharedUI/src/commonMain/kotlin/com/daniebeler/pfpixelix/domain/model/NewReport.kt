package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewReport (
    @SerialName("report_type") val reportType: String,
    @SerialName("object_id") val objectId: String,
    @SerialName("object_type") val objectType: ReportObjectType,
)

enum class ReportObjectType {
    @SerialName("post") POST,
    @SerialName("user") User
}