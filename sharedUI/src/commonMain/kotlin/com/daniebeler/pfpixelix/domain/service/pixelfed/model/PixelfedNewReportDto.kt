package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.ReportObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNewReportDto(
    @SerialName("report_type") val reportType: String,
    @SerialName("object_id") val objectId: String,
    @SerialName("object_type") val objectType: PixelfedReportObjectTypeDto
)

@Serializable
enum class PixelfedReportObjectTypeDto {
    @SerialName("post") POST,
    @SerialName("user") USER
}

// --- MAPPING EXTENSIONS ---

fun PixelfedNewReportDto.toDomain(): NewReport {
    return NewReport(
        reportType = this.reportType,
        objectId = this.objectId,
        objectType = this.objectType.toDomain()
    )
}

fun NewReport.toDto(): PixelfedNewReportDto {
    return PixelfedNewReportDto(
        reportType = this.reportType,
        objectId = this.objectId,
        objectType = this.objectType.toDto()
    )
}

fun PixelfedReportObjectTypeDto.toDomain(): ReportObjectType = when (this) {
    PixelfedReportObjectTypeDto.POST -> ReportObjectType.POST
    PixelfedReportObjectTypeDto.USER -> ReportObjectType.USER
}

fun ReportObjectType.toDto(): PixelfedReportObjectTypeDto = when (this) {
    ReportObjectType.POST -> PixelfedReportObjectTypeDto.POST
    ReportObjectType.USER -> PixelfedReportObjectTypeDto.USER
}