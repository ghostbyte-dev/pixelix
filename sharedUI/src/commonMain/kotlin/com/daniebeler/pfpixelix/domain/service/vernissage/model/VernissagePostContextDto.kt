package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.PostContext
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedPostDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissagePostContextDto(
    @SerialName("ancestors") val ancestors: List<VernissagePostDto> = emptyList(),
    @SerialName("descendants") val descendants: List<VernissagePostDto> = emptyList()
)

fun VernissagePostContextDto.toDomain(): PostContext {
    return PostContext(
        ancestors = this.ancestors.map { it.toDomain() },
        descendants = this.descendants.map { it.toDomain() }
    )
}