package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Search
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedAccountDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedPostDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedTagDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageSearchDto(
    @SerialName("users") val users: List<VernissageAccountDto> = emptyList(),
    @SerialName("statuses") val posts: List<VernissagePostDto> = emptyList(),
    @SerialName("hashtags") val tags: List<VernissageTagDto> = emptyList()
)

fun VernissageSearchDto.toDomain(): Search {
    return Search(
        accounts = this.users.map { it.toDomain() },
        posts = this.posts.map { it.toDomain() },
        tags = this.tags.map { it.toDomain() }
    )
}