package com.daniebeler.pfpixelix.domain.model

data class NewReport(
    val reportType: String,
    val objectId: String,
    val objectType: ReportObjectType
)

enum class ReportObjectType {
    POST, USER
}