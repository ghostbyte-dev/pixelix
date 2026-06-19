package com.daniebeler.pfpixelix.domain.service.vernissage.model.request

import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageVisibilityDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageReblogRequest(
    @SerialName("visibility") val visibility: VernissageVisibilityDto
)