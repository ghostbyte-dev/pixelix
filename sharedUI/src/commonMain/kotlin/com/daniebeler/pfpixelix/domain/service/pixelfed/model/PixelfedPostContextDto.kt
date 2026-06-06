package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.PostContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedPostContextDto(
    @SerialName("ancestors") val ancestors: List<PixelfedPostDto> = emptyList(),
    @SerialName("descendants") val descendants: List<PixelfedPostDto> = emptyList()
)

fun PixelfedPostContextDto.toDomain(): PostContext {
    return PostContext(
        ancestors = this.ancestors.map { it.toDomain() },
        descendants = this.descendants.map { it.toDomain() }
    )
}